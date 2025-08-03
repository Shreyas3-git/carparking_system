package com.demo.design.carparking.contract.impl;

import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.Vehicle;
import com.demo.design.carparking.entity.VehicleType;

import java.util.*;

public class NearestFirstAllocationStrategy implements ParkingAllocationStrategy {
    private final Map<VehicleType, List<ParkingSpotType>> compatibilityMap;

    public NearestFirstAllocationStrategy() {
        compatibilityMap = new HashMap<>();
        compatibilityMap.put(VehicleType.MOTORCYCLE, List.of(ParkingSpotType.SMALL, ParkingSpotType.MEDIUM, ParkingSpotType.LARGE));
        compatibilityMap.put(VehicleType.CAR, List.of(ParkingSpotType.MEDIUM, ParkingSpotType.LARGE));
        compatibilityMap.put(VehicleType.BUS, List.of(ParkingSpotType.LARGE));
    }

    @Override
    public Optional<ParkingSpot> findParkingSpot(List<ParkingSpot> spots, Vehicle vehicle) {
        List<ParkingSpotType> compatibleTypes = compatibilityMap.get(vehicle.getType());
        return spots.stream()
                .filter(spot -> !spot.isOccupied() && compatibleTypes.contains(spot.getType()))
                .min(Comparator.comparingLong(ParkingSpot::getId));
    }
}
