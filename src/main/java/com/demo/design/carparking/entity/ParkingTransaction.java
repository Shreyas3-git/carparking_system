package com.demo.design.carparking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_transactions")
public class ParkingTransaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vehicle_license_plate", referencedColumnName = "license_plate")
    private Vehicle vehicle;

    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;

    private ParkingTransaction() {}

    public Long getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getFee() { return fee; }

    public void completeTransaction(LocalDateTime exitTime, double fee) {
        this.exitTime = exitTime;
        this.fee = fee;
    }

    public static class ParkingTransactionBuilder {
        private Vehicle vehicle;
        private LocalDateTime entryTime;

        public ParkingTransactionBuilder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public ParkingTransactionBuilder entryTime(LocalDateTime entryTime) {
            this.entryTime = entryTime;
            return this;
        }

        public ParkingTransaction build() {
            ParkingTransaction transaction = new ParkingTransaction();
            transaction.vehicle = this.vehicle;
            transaction.entryTime = this.entryTime;
            return transaction;
        }
    }
}
