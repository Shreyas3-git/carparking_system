-- Create the parking_floors table
CREATE TABLE IF NOT EXISTS smart_parking.parking_floors (
  floor_id BIGINT NOT NULL AUTO_INCREMENT,
  floor_name VARCHAR(255) NOT NULL,
  total_spots INT DEFAULT 0,
  PRIMARY KEY (floor_id)
);

-- Creates the vehicles table, which is a dependency for parking_reservations
CREATE TABLE IF NOT EXISTS smart_parking.vehicles (
  license_plate VARCHAR(255) NOT NULL,
  model VARCHAR(255) DEFAULT NULL,
  owner_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (license_plate)
);

-- Creates the parking_spots table with corrected columns
CREATE TABLE IF NOT EXISTS smart_parking.parking_spots (
  spot_id BIGINT NOT NULL AUTO_INCREMENT,
  floor_id BIGINT NOT NULL,
  type ENUM('SMALL', 'MEDIUM', 'LARGE') NOT NULL,
  occupied BOOLEAN NOT NULL DEFAULT FALSE,
  vehicle_license_plate VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (spot_id)
);

-- Creates the parking_reservations table based on the ParkingReservation entity
CREATE TABLE IF NOT EXISTS smart_parking.parking_reservations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  expiry_time DATETIME(6) DEFAULT NULL,
  reservation_time DATETIME(6) DEFAULT NULL,
  status ENUM('ACTIVE','CANCELLED','EXPIRED','FULFILLED') DEFAULT NULL,
  spot_id BIGINT DEFAULT NULL,
  vehicle_license_plate VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX UK_spot_id_reservation(spot_id),
  UNIQUE INDEX UK_vehicle_license_plate(vehicle_license_plate)
);

-- Creates the parking_transactions table based on the ParkingTransaction entity
CREATE TABLE IF NOT EXISTS smart_parking.parking_transactions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  vehicle_license_plate VARCHAR(255) DEFAULT NULL,
  entry_time DATETIME(6) DEFAULT NULL,
  exit_time DATETIME(6) DEFAULT NULL,
  fee DOUBLE DEFAULT NULL,
  PRIMARY KEY (id)
);

