package com.demo.design.carparking.repository;

import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.ParkingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    List<ParkingSpot> findAllByOccupiedFalse();

    List<ParkingSpot> findByTypeAndOccupiedFalse(ParkingSpotType type);

    List<ParkingSpot> findByTypeAndOccupiedFalseAndFloorId(ParkingSpotType type, Long floorId);
}
