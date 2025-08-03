package com.demo.design.carparking.contract;

import com.demo.design.carparking.entity.ParkingFloor;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface FloorSelectionStrategy
{
    Optional<ParkingFloor> selectFloor(List<ParkingFloor> floors, Vehicle vehicle);
    List<ParkingSpotType> getCompatibleSpotTypes(Vehicle vehicle);

}
