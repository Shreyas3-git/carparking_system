package com.demo.design.carparking.contract;

import com.demo.design.carparking.entity.Vehicle;

import java.time.LocalDateTime;

public interface FeeCalculationStrategy
{
    double calculateFee(Vehicle vehicle, LocalDateTime exitTime);
}
