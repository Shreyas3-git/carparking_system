package com.demo.design.carparking.errorhandling;

import org.springframework.http.HttpStatus;

public class ParkingSpotUnavailableException extends CarParkingException
{

    public ParkingSpotUnavailableException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ParkingSpotUnavailableException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
