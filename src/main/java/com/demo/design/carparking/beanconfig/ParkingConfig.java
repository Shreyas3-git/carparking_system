package com.demo.design.carparking.beanconfig;

import com.demo.design.carparking.contract.FeeCalculationStrategy;
import com.demo.design.carparking.contract.FloorSelectionStrategy;
import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.contract.ReservationStrategy;
import com.demo.design.carparking.contract.impl.HourlyFeeCalculationStrategy;
import com.demo.design.carparking.contract.impl.MinimumAvailableSpotsFloorStrategy;
import com.demo.design.carparking.contract.impl.NearestFirstAllocationStrategy;
import com.demo.design.carparking.contract.impl.TimeBasedReservationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParkingConfig
{
    @Bean
    public ParkingAllocationStrategy parkingAllocationStrategy() {
        return new NearestFirstAllocationStrategy();
    }

    @Bean
    public FeeCalculationStrategy feeCalculationStrategy() {
        return new HourlyFeeCalculationStrategy();
    }

    @Bean
    public ReservationStrategy reservationStrategy(ParkingAllocationStrategy allocationStrategy) {
        return new TimeBasedReservationStrategy(allocationStrategy);
    }

    @Bean
    public FloorSelectionStrategy floorSelectionStrategy() {
        return new MinimumAvailableSpotsFloorStrategy();
    }
}
