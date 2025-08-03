package com.demo.design.carparking.repository;

import com.demo.design.carparking.entity.ParkingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingReservationRepository extends JpaRepository<ParkingReservation,Long> {
    @Query("SELECT pr FROM ParkingReservation pr WHERE pr.vehicle.licensePlate = :licensePlate AND pr.status = 'ACTIVE'")
    Optional<ParkingReservation> findActiveByVehicle(@Param("licensePlate") String licensePlate);

}
