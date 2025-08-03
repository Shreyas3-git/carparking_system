package com.demo.design.carparking.contract.impl;

import com.demo.design.carparking.contract.FloorSelectionStrategy;
import com.demo.design.carparking.entity.ParkingFloor;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.Vehicle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MinimumAvailableSpotsFloorStrategy implements FloorSelectionStrategy {

    @Override
    public List<ParkingSpotType> getCompatibleSpotTypes(Vehicle vehicle) {
        return switch (vehicle.getType()) {
            case MOTORCYCLE -> List.of(ParkingSpotType.SMALL, ParkingSpotType.MEDIUM, ParkingSpotType.LARGE);
            case CAR -> List.of(ParkingSpotType.MEDIUM, ParkingSpotType.LARGE);
            case BUS -> List.of(ParkingSpotType.LARGE);
            default -> List.of();
        };
    }
    @Override
    public Optional<ParkingFloor> selectFloor(List<ParkingFloor> floors, Vehicle vehicle) {
        List<ParkingSpotType> compatibleTypes = getCompatibleSpotTypes(vehicle);
        return floors.stream()
                .filter(floor -> floor.getParkingSpots() != null)
                .min(Comparator.comparingInt(floor ->
                        (int) floor.getParkingSpots().stream()
                                .filter(spot -> !spot.isOccupied() && compatibleTypes.contains(spot.getType()))
                                .count()));
    }
}
