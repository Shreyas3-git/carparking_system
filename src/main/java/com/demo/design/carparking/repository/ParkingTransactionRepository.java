package com.demo.design.carparking.repository;

import com.demo.design.carparking.entity.ParkingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingTransactionRepository extends JpaRepository<ParkingTransaction,Long> {
}
