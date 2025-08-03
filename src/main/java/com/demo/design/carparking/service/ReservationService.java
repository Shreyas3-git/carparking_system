package com.demo.design.carparking.service;

import com.demo.design.carparking.contract.ReservationStrategy;
import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.*;
import com.demo.design.carparking.errorhandling.ReservationException;
import com.demo.design.carparking.repository.ParkingReservationRepository;
import com.demo.design.carparking.repository.ParkingSpotRepository;
import com.demo.design.carparking.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService
{
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ParkingSpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingReservationRepository reservationRepository;
    private final ReservationStrategy reservationStrategy;

    public ReservationService(
            ParkingSpotRepository spotRepository,
            VehicleRepository vehicleRepository,
            ParkingReservationRepository reservationRepository,
            ReservationStrategy reservationStrategy) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.reservationRepository = reservationRepository;
        this.reservationStrategy = reservationStrategy;
    }


    @Transactional
    public ResponseEntity<CommonResponse> reserveSpot(Vehicle vehicle, Long floorId) {
        log.info("Reserving spot for vehicle: {} on floor: {}", vehicle.getLicensePlate(), floorId);
        vehicleRepository.save(vehicle);

        ParkingSpotType parkingSpotType;
        if(vehicle.getType().equals(VehicleType.MOTORCYCLE)) {
            parkingSpotType = ParkingSpotType.SMALL;
        } else if (vehicle.getType().equals(VehicleType.CAR)) {
            parkingSpotType = ParkingSpotType.MEDIUM;
        } else {
            parkingSpotType = ParkingSpotType.LARGE;
        }
        List<ParkingSpot> availableSpots = spotRepository.findByTypeAndOccupiedFalseAndFloorId(parkingSpotType, floorId);
        ParkingReservation reservation = reservationStrategy.reserveSpot(availableSpots, vehicle);

        reservationRepository.save(reservation);
        spotRepository.save(reservation.getReservedSpot());

        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message("Reservation successful. Spot: " + reservation.getReservedSpot().getId() + " on floor " + floorId)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<CommonResponse> cancelReservation(String licensePlate) {
        log.info("Cancelling reservation for vehicle: {}", licensePlate);
        ParkingReservation reservation = reservationRepository.findActiveByVehicle(licensePlate)
                .orElseThrow(() -> new ReservationException("No active reservation found for vehicle: " + licensePlate, HttpStatus.NOT_FOUND));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.getReservedSpot().release();
        reservationRepository.save(reservation);
        spotRepository.save(reservation.getReservedSpot());

        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message("Reservation cancelled for vehicle: " + licensePlate)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
