package com.demo.design.carparking.repository;

import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ParkingSpotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    List<ParkingSpot> findAllByOccupiedFalse();

    Collection<Object> findByTypeAndOccupiedFalse(ParkingSpotType type);
}
