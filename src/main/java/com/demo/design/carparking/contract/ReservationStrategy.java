package com.demo.design.carparking.contract;

import com.demo.design.carparking.entity.ParkingReservation;
import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.Vehicle;

import java.util.List;

public interface ReservationStrategy
{
    ParkingReservation reserveSpot(List<ParkingSpot> spots, Vehicle vehicle);
}
