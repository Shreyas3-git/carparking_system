package com.demo.design.carparking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_spot")
public class ParkingSpot
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ParkingSpotType type;

    private boolean occupied;

    @OneToOne
    @JoinColumn(name = "vehicle_license_plate")
    private Vehicle vehicle;

    private ParkingSpot() {}

    public Long getId() { return id; }
    public ParkingSpotType getType() { return type; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getVehicle() { return vehicle; }

    public void occupy(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.occupied = true;
    }

    public void release() {
        this.vehicle = null;
        this.occupied = false;
    }

    public static class ParkingSpotBuilder {
        private ParkingSpotType type;
        private boolean occupied;
        private Vehicle vehicle;

        public ParkingSpotBuilder type(ParkingSpotType type) {
            this.type = type;
            return this;
        }

        public ParkingSpotBuilder occupied(boolean occupied) {
            this.occupied = occupied;
            return this;
        }

        public ParkingSpotBuilder vehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public ParkingSpot build() {
            ParkingSpot parkingSpot = new ParkingSpot();
            parkingSpot.type = this.type;
            parkingSpot.vehicle = this.vehicle;
            parkingSpot.occupied = this.occupied;
            return parkingSpot;
        }
    }

}
