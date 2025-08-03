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
    @JoinColumn(name = "license_plate")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private ParkingFloor floor;

    private ParkingSpot() {}

    public Long getId() { return id; }
    public ParkingSpotType getType() { return type; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getVehicle() { return vehicle; }

    public ParkingFloor getFloor() {
        return floor;
    }

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
        private ParkingFloor floor;
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

        public ParkingSpotBuilder floor(ParkingFloor floor) {
            this.floor = floor;
            return this;
        }

        public ParkingSpot build() {
            ParkingSpot parkingSpot = new ParkingSpot();
            parkingSpot.type = this.type;
            parkingSpot.vehicle = this.vehicle;
            parkingSpot.occupied = this.occupied;
            parkingSpot.floor = this.floor;
            return parkingSpot;
        }
    }

}
