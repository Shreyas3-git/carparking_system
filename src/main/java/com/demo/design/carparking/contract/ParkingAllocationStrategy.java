package com.demo.design.carparking.contract;

import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface ParkingAllocationStrategy
{
    Optional<ParkingSpot> findParkingSpot(List<ParkingSpot> spots, Vehicle vehicle);
}
