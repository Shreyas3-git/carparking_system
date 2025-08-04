package com.demo.design.carparking.service;

import com.demo.design.carparking.contract.FloorSelectionStrategy;
import com.demo.design.carparking.contract.ParkingAllocationStrategy;
import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.*;
import com.demo.design.carparking.errorhandling.ParkingSpotUnavailableException;
import com.demo.design.carparking.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        when(reservationRepository.findActiveByVehicle(vehicle.getLicensePlate()))
                .thenReturn(Optional.empty());
        when(floorRepository.findById(floorId))
                .thenReturn(Optional.of(floor));
        when(floorSelectionStrategy.selectFloor(anyList(),eq(vehicle)))
                .thenReturn(Optional.of(floor));
        when(spotRepository.findByTypeAndOccupiedFalseAndFloorId(spot.getType(),floorId))
                .thenReturn(List.of(spot));
        when(allocationStrategy.findParkingSpot(anyList(),eq(vehicle)))
                .thenReturn(Optional.of(spot));

        ResponseEntity<CommonResponse> response = checkInService.checkIn(vehicle,floorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        verify(vehicleRepository.save(vehicle),times(1));
        verify(spotRepository).save(spot);
        verify(reservationRepository.save(any(ParkingReservation.class)));
        verify(transactionRepository).save(any(ParkingTransaction.class));
    }

    @Test
    @DisplayName("Should use active reservation when available")
    void checkIn_ShouldUseReservation_WhenActiveReservationExists() {
        Long floorId = 1L;
        ParkingReservation reservation = createActiveReservation();
        when(reservationRepository.findActiveByVehicle(vehicle.getLicensePlate()))
                .thenReturn(Optional.of(reservation));

        ResponseEntity<CommonResponse> result = checkInService.checkIn(vehicle, floorId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getMessage()).contains("using reservation");
        verify(reservationRepository).save(argThat(r ->
                r.getStatus() == ReservationStatus.FULFILLED));
    }

    @Test
    @DisplayName("Should handle expired reservation")
    void checkIn_ShouldHandleExpiredReservation_WhenReservationIsExpired() {
        Long floorId = 1L;
        ParkingReservation expiredReservation = createExpiredReservation();
        when(reservationRepository.findActiveByVehicle(vehicle.getLicensePlate()))
                .thenReturn(Optional.of(expiredReservation));
        when(floorRepository.findById(floorId))
                .thenReturn(Optional.of(floor));
        setupSuccessfulCheckIn(floorId);

        ResponseEntity<CommonResponse> result = checkInService.checkIn(vehicle, floorId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(reservationRepository).save(argThat(r ->
                r.getStatus() == ReservationStatus.EXPIRED));
        verify(spotRepository).save(argThat(s -> !s.isOccupied()));
    }

    @Test
    @DisplayName("Should throw exception when no floor available")
    void checkIn_ShouldThrowException_WhenNoFloorAvailable() {
        Long floorId = 1L;
        when(reservationRepository.findActiveByVehicle(vehicle.getLicensePlate()))
                .thenReturn(Optional.empty());
        when(floorRepository.findById(floorId))
                .thenReturn(Optional.of(floor));
        when(floorSelectionStrategy.selectFloor(anyList(), eq(vehicle)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.checkIn(vehicle, floorId))
                .isInstanceOf(ParkingSpotUnavailableException.class)
                .hasMessageContaining("No available floors");
    }

    @Test
    @DisplayName("Should return correct available spots count")
    void getAvailableSpotsCount_ShouldReturnCorrectCount() {
        ParkingSpotType spotType = ParkingSpotType.MEDIUM;
        List<ParkingSpot> availableSpots = List.of(spot, createTestSpot());
        when(spotRepository.findByTypeAndOccupiedFalse(spotType))
                .thenReturn(availableSpots);

        long count = checkInService.getAvailableSpotsCount(spotType);
        assertThat(count).isEqualTo(2);
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
