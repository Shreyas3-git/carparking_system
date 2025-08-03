package com.demo.design.carparking.contract.impl;

import com.demo.design.carparking.contract.FeeCalculationStrategy;
import com.demo.design.carparking.entity.Vehicle;
import com.demo.design.carparking.entity.VehicleType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class HourlyFeeCalculationStrategy implements FeeCalculationStrategy
{
    private final Map<VehicleType, Double> rates;

    public HourlyFeeCalculationStrategy() {
        rates = new HashMap<>();
        rates.put(VehicleType.MOTORCYCLE, 2.0);
        rates.put(VehicleType.CAR, 5.0);
        rates.put(VehicleType.BUS, 10.0);
    }

    @Override
    public double calculateFee(Vehicle vehicle, LocalDateTime exitTime) {
        long hours = ChronoUnit.HOURS.between(vehicle.getEntryTime(), exitTime);
        if (hours == 0) hours = 1;
        return rates.get(vehicle.getType()) * hours;
    }
}
