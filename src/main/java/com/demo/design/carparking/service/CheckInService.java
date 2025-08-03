package com.demo.design.carparking.service;

import com.demo.design.carparking.contract.FeeCalculationStrategy;
import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.ParkingTransaction;
import com.demo.design.carparking.entity.Vehicle;
import com.demo.design.carparking.errorhandling.ParkingSpotUnavailableException;
import com.demo.design.carparking.repository.ParkingSpotRepository;
import com.demo.design.carparking.repository.ParkingTransactionRepository;
import com.demo.design.carparking.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService
{
    private static final Logger log = LoggerFactory.getLogger(CheckInService.class);

    @Autowired
    private ParkingSpotRepository spotRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ParkingTransactionRepository transactionRepository;
    @Autowired
    private ParkingAllocationStrategy allocationStrategy;
    @Autowired
    private FeeCalculationStrategy feeStrategy;

    @Transactional
    public ResponseEntity<CommonResponse> checkIn(Vehicle vehicle) {
        log.info("vehicle checkin with number plate: {} at {}",vehicle.getLicensePlate(),vehicle.getEntryTime());
        vehicleRepository.save(vehicle);
        List<ParkingSpot> availableSpots = spotRepository.findAllByOccupiedFalse();
        Optional<ParkingSpot> spot = allocationStrategy.findParkingSpot(availableSpots, vehicle);
        if (spot.isPresent()) {
            ParkingSpot parkingSpot = spot.get();
            parkingSpot.occupy(vehicle);
            spotRepository.save(parkingSpot);

            ParkingTransaction transaction = new ParkingTransaction.ParkingTransactionBuilder()
                    .vehicle(vehicle)
                    .entryTime(vehicle.getEntryTime())
                    .build();
            transactionRepository.save(transaction);
            CommonResponse response = new CommonResponse.CommonResponseBuilder()
                    .message("Check-in successful. Spot allocated: " + parkingSpot.getId())
                    .status("SUCCESS")
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
        }
        throw new ParkingSpotUnavailableException("No available parking spot for this vehicle type.");
    }

    public long getAvailableSpotsCount(ParkingSpotType type) {
        return spotRepository.findByTypeAndOccupiedFalse(type).size();
    }

}
