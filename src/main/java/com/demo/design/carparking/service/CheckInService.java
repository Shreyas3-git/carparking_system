package com.demo.design.carparking.service;

import com.demo.design.carparking.contract.FloorSelectionStrategy;
import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.*;
import com.demo.design.carparking.errorhandling.ParkingSpotUnavailableException;
import com.demo.design.carparking.errorhandling.UnSupportedVehicleException;
import com.demo.design.carparking.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService
{
    private static final Logger log = LoggerFactory.getLogger(CheckInService.class);

    private final ParkingSpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingTransactionRepository transactionRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingReservationRepository reservationRepository;
    private final ParkingAllocationStrategy allocationStrategy;
    private final FloorSelectionStrategy floorSelectionStrategy;

    @Autowired
    public CheckInService(
            ParkingSpotRepository spotRepository,
            VehicleRepository vehicleRepository,
            ParkingTransactionRepository transactionRepository,
            ParkingFloorRepository floorRepository,
            ParkingReservationRepository reservationRepository,
            ParkingAllocationStrategy allocationStrategy,
            FloorSelectionStrategy floorSelectionStrategy) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.transactionRepository = transactionRepository;
        this.floorRepository = floorRepository;
        this.reservationRepository = reservationRepository;
        this.allocationStrategy = allocationStrategy;
        this.floorSelectionStrategy = floorSelectionStrategy;
    }

    @Transactional
    public ResponseEntity<CommonResponse> checkIn(Vehicle vehicle, Long floorId) {
        log.info("Vehicle check-in with license plate: {} at {}", vehicle.getLicensePlate(), vehicle.getEntryTime());
        vehicleRepository.save(vehicle);

        Optional<ParkingReservation> activeReservation = reservationRepository.findActiveByVehicle(vehicle.getLicensePlate());
        if (activeReservation.isPresent()) {
            ParkingReservation reservation = activeReservation.get();
            if (reservation.getExpiryTime().isBefore(LocalDateTime.now())) {
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservation.getReservedSpot().release();
                reservationRepository.save(reservation);
                spotRepository.save(reservation.getReservedSpot());
            } else {
                ParkingSpot reservedSpot = reservation.getReservedSpot();
                reservation.setStatus(ReservationStatus.FULFILLED);
                reservationRepository.save(reservation);

                ParkingTransaction transaction = new ParkingTransaction.ParkingTransactionBuilder()
                        .vehicle(vehicle)
                        .entryTime(vehicle.getEntryTime())
                        .build();
                transactionRepository.save(transaction);

                CommonResponse response = new CommonResponse.CommonResponseBuilder()
                        .message("Check-in successful using reservation. Spot allocated: " + reservedSpot.getId() + " on floor " + reservedSpot.getFloor().getId())
                        .status("SUCCESS")
                        .timestamp(LocalDateTime.now())
                        .build();
                return ResponseEntity.ok(response);
            }
        }
        List<ParkingFloor> floors = floorRepository.findById(floorId)
                .map(List::of)
                .orElseThrow(() -> new IllegalArgumentException("Invalid floor ID: " + floorId));



        Optional<ParkingFloor> selectedFloor = floorSelectionStrategy.selectFloor(floors, vehicle);
        if (selectedFloor.isEmpty()) {
            throw new ParkingSpotUnavailableException("No available floors for this vehicle type");
        }

        ParkingFloor floor = selectedFloor.get();
        List<ParkingSpot> availableSpots = spotRepository.findByTypeAndOccupiedFalseAndFloorId(getAvailableParkingSpot(vehicle), floor.getId());
        Optional<ParkingSpot> spot = allocationStrategy.findParkingSpot(availableSpots, vehicle);

        if (spot.isEmpty()) {
            throw new ParkingSpotUnavailableException("No available parking spot on floor " + floorId);
        }

        ParkingSpot parkingSpot = spot.get();
        parkingSpot.occupy(vehicle);
        spotRepository.save(parkingSpot);

        ParkingTransaction transaction = new ParkingTransaction.ParkingTransactionBuilder()
                .vehicle(vehicle)
                .entryTime(vehicle.getEntryTime())
                .build();
        transactionRepository.save(transaction);

        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message("Check-in successful. Spot allocated: " + parkingSpot.getId() + " on floor " + floorId)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    public long getAvailableSpotsCount(ParkingSpotType type) {
        return spotRepository.findByTypeAndOccupiedFalse(type).size();
    }

    public ParkingSpotType getAvailableParkingSpot(Vehicle vehicle) {
        return floorSelectionStrategy.getCompatibleSpotTypes(vehicle)
                .stream()
                .findFirst()
                .orElseThrow(() -> new UnSupportedVehicleException("Enter Vehicle type: %s" +vehicle.getType()+" is unsupported", HttpStatus.BAD_REQUEST));
    }
}
