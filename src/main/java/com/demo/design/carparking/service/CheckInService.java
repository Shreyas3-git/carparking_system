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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService
{
    private final ParkingSpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingTransactionRepository transactionRepository;
    private final ParkingAllocationStrategy allocationStrategy;
    private final FeeCalculationStrategy feeStrategy;

    public CheckInService(ParkingSpotRepository spotRepository, VehicleRepository vehicleRepository, ParkingTransactionRepository transactionRepository, ParkingAllocationStrategy allocationStrategy, FeeCalculationStrategy feeStrategy) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.transactionRepository = transactionRepository;
        this.allocationStrategy = allocationStrategy;
        this.feeStrategy = feeStrategy;
    }

    @Transactional
    public ResponseEntity<CommonResponse> checkIn(Vehicle vehicle) {
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
