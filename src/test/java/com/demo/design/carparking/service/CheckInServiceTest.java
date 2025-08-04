package com.demo.design.carparking.service;

import com.demo.design.carparking.contract.FloorSelectionStrategy;
import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.entity.*;
import com.demo.design.carparking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckInServiceTest")
public class CheckInServiceTest
{
    @Mock
    private ParkingSpotRepository spotRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ParkingTransactionRepository transactionRepository;
    @Mock
    private ParkingFloorRepository floorRepository;
    @Mock
    private ParkingReservationRepository reservationRepository;
    @Mock
    private ParkingAllocationStrategy allocationStrategy;
    @Mock
    private FloorSelectionStrategy floorSelectionStrategy;
    @InjectMocks
    private CheckInService checkInService;

    private Vehicle vehicle;
    private ParkingFloor floor;
    private ParkingSpot spot;

    @BeforeEach
    public void setUp() {
        vehicle = createTestVehicle();
        floor = createTestFloor();
        spot = createTestSpot();
    }

    @Test
    @DisplayName("checkIn_ShouldReturnSuccess_WhenNoReservationExists")
    public void checkIn_ShouldReturnSuccess_WhenNoReservationExists() {
        Long floorId = 1l;
        
    }

    private Vehicle createTestVehicle() {
        return new Vehicle.VehicleBuilder()
                .licensePlate("TEST123")
                .type(VehicleType.CAR)
                .entryTime(LocalDateTime.now())
                .build();
    }

    private ParkingFloor createTestFloor() {
        return new ParkingFloor.ParkingFloorBuilder()
                .totalSpots(100)
                .build();
    }

    private ParkingSpot createTestSpot() {
        return new ParkingSpot.ParkingSpotBuilder()
                .type(ParkingSpotType.MEDIUM)
                .occupied(false)
                .floor(floor)
                .build();
    }

    private ParkingReservation createActiveReservation() {
        return new ParkingReservation.ParkingReservationBuilder()
                .vehicle(vehicle)
                .reservedSpot(spot)
                .status(ReservationStatus.ACTIVE)
                .reservationTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusHours(1))
                .build();
    }

    private ParkingReservation createExpiredReservation() {
        return new ParkingReservation.ParkingReservationBuilder()
                .vehicle(vehicle)
                .reservedSpot(spot)
                .status(ReservationStatus.ACTIVE)
                .reservationTime(LocalDateTime.now().minusHours(2))
                .expiryTime(LocalDateTime.now().minusHours(1))
                .build();
    }

    private void setupSuccessfulCheckIn(Long floorId) {
        when(floorSelectionStrategy.selectFloor(anyList(), eq(vehicle)))
                .thenReturn(Optional.of(floor));
        when(spotRepository.findByTypeAndOccupiedFalseAndFloorId(any(), eq(floorId)))
                .thenReturn(List.of(spot));
        when(allocationStrategy.findParkingSpot(anyList(), eq(vehicle)))
                .thenReturn(Optional.of(spot));
    }
}
