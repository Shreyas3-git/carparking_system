package com.demo.design.carparking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_reservations")
public class ParkingReservation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "spot_id", referencedColumnName = "id")
    private ParkingSpot reservedSpot;

    @OneToOne
    @JoinColumn(name = "vehicle_license_plate", referencedColumnName = "license_plate")
    private Vehicle vehicle;

    private LocalDateTime reservationTime;
    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private ParkingReservation() {}

    public Long getId() {
        return id;
    }

    public ParkingSpot getReservedSpot() {
        return reservedSpot;
    }

    public void setReservedSpot(ParkingSpot reservedSpot) {
        this.reservedSpot = reservedSpot;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public static class ParkingReservationBuilder {
        private ParkingSpot reservedSpot;
        private Vehicle vehicle;
        private LocalDateTime reservationTime;
        private LocalDateTime expiryTime;
        private ReservationStatus status;

        public ParkingReservationBuilder reservedSpot(ParkingSpot reservedSpot) {
            this.reservedSpot = reservedSpot;
            return this;
        }

        public ParkingReservationBuilder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public ParkingReservationBuilder reservationTime(LocalDateTime reservationTime) {
            this.reservationTime = reservationTime;
            return this;
        }

        public ParkingReservationBuilder expiryTime(LocalDateTime expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public ParkingReservationBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ParkingReservation build() {
            ParkingReservation reservation = new ParkingReservation();
            reservation.reservedSpot = this.reservedSpot;
            reservation.vehicle = this.vehicle;
            reservation.reservationTime = this.reservationTime;
            reservation.expiryTime = this.expiryTime;
            reservation.status = this.status;
            return reservation;
        }
    }
}
