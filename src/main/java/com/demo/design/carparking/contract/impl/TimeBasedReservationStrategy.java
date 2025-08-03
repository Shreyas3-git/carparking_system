package com.demo.design.carparking.contract.impl;

import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.contract.ReservationStrategy;
import com.demo.design.carparking.entity.ParkingReservation;
import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ReservationStatus;
import com.demo.design.carparking.entity.Vehicle;
import com.demo.design.carparking.errorhandling.ParkingSpotUnavailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TimeBasedReservationStrategy implements ReservationStrategy {

    private final ParkingAllocationStrategy allocationStrategy;
    private final long reservationDurationMinutes;

    public TimeBasedReservationStrategy(ParkingAllocationStrategy allocationStrategy) {
        this.allocationStrategy = allocationStrategy;
        this.reservationDurationMinutes = 60;
    }
    @Override
    public ParkingReservation reserveSpot(List<ParkingSpot> spots, Vehicle vehicle) {
        Optional<ParkingSpot> spot = allocationStrategy.findParkingSpot(spots, vehicle);
        if (spot.isEmpty()) {
            throw new ParkingSpotUnavailableException("No available parking spot for reservation");
        }
        ParkingSpot reservedSpot = spot.get();
        reservedSpot.occupy(vehicle);

        return new ParkingReservation.ParkingReservationBuilder()
                .reservedSpot(reservedSpot)
                .vehicle(vehicle)
                .reservationTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(reservationDurationMinutes))
                .status(ReservationStatus.ACTIVE)
                .build();
    }
}
