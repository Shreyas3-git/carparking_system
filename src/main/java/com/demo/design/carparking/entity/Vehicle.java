package com.demo.design.carparking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
public class Vehicle
{
    @Id
    @NotNull(message = "licensePlate can't be null")
    @NotBlank(message = "licensePlate can't be balnk")
    @Column(name = "license_plate")
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "VehicleType can't be null")
    private VehicleType type;

    @NotNull(message = "entryTime can't be null")
    private LocalDateTime entryTime;

    private Vehicle() {}

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
    public LocalDateTime getEntryTime() { return entryTime; }


    public static class VehicleBuilder {
        private String licensePlate;
        private VehicleType type;
        private LocalDateTime entryTime;

        public VehicleBuilder licensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
            return this;
        }

        public VehicleBuilder type(VehicleType type) {
            this.type = type;
            return this;
        }

        public VehicleBuilder entryTime(LocalDateTime entryTime) {
            this.entryTime = entryTime;
            return this;
        }

        public Vehicle build() {
            Vehicle vehicle = new Vehicle();
            vehicle.licensePlate = this.licensePlate;
            vehicle.type = this.type;
            vehicle.entryTime = this.entryTime;
            return vehicle;
        }
    }
}
