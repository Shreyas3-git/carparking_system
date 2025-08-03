package com.demo.design.carparking.service;

import com.demo.design.carparking.contract.FeeCalculationStrategy;
import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ParkingTransaction;
import com.demo.design.carparking.entity.Vehicle;
import com.demo.design.carparking.errorhandling.NotFoundException;
import com.demo.design.carparking.repository.ParkingSpotRepository;
import com.demo.design.carparking.repository.ParkingTransactionRepository;
import com.demo.design.carparking.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckoutService
{
    private final ParkingSpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingTransactionRepository transactionRepository;
    private final ParkingAllocationStrategy allocationStrategy;
    private final FeeCalculationStrategy feeStrategy;

    public CheckoutService(ParkingSpotRepository spotRepository, VehicleRepository vehicleRepository, ParkingTransactionRepository transactionRepository, ParkingAllocationStrategy allocationStrategy, FeeCalculationStrategy feeStrategy) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.transactionRepository = transactionRepository;
        this.allocationStrategy = allocationStrategy;
        this.feeStrategy = feeStrategy;
    }


    @Transactional
    public ResponseEntity<CommonResponse> checkOut(String licensePlate, LocalDateTime exitTime) {
        Vehicle vehicle = vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + licensePlate));

        ParkingSpot spot = spotRepository.findAll().stream()
                .filter(s -> s.getVehicle() != null && s.getVehicle().getLicensePlate().equals(licensePlate))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Parking spot not found"));

        double fee = feeStrategy.calculateFee(vehicle, exitTime);
        spot.release();
        spotRepository.save(spot);

        ParkingTransaction transaction = transactionRepository.findAll().stream()
                .filter(t -> t.getVehicle().getLicensePlate().equals(licensePlate))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Parking transaction not found for vehicle: " + licensePlate));

        transaction.completeTransaction(exitTime, fee);
        transactionRepository.save(transaction);

        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message("Checkout successful. Fee: " + fee)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

}
