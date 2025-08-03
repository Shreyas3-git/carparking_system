package com.demo.design.carparking.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "parking_floors")
public class ParkingFloor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floor_id")
    private Long id;

    private Integer totalSpots;

    @OneToMany(mappedBy = "floor",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParkingSpot> parkingSpots;

    private ParkingFloor() {}

    public Long getId() { return id; }
    public Integer getTotalSpots() { return totalSpots; }
    public List<ParkingSpot> getParkingSpots() { return parkingSpots; }

    public void setTotalSpots(Integer totalSpots) { this.totalSpots = totalSpots; }
    public void setParkingSpots(List<ParkingSpot> parkingSpots) { this.parkingSpots = parkingSpots; }

    public static class ParkingFloorBuilder {
        private Integer totalSpots;
        private List<ParkingSpot> parkingSpots;


        public ParkingFloorBuilder totalSpots(Integer totalSpots) {
            this.totalSpots = totalSpots;
            return this;
        }

        public ParkingFloorBuilder parkingSpots(List<ParkingSpot> parkingSpots) {
            this.parkingSpots = parkingSpots;
            return this;
        }

        public ParkingFloor build() {
            ParkingFloor floor = new ParkingFloor();
            floor.totalSpots = this.totalSpots;
            floor.parkingSpots = this.parkingSpots;
            return floor;
        }
    }
}