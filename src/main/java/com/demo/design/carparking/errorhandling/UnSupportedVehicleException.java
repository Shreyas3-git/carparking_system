package com.demo.design.carparking.errorhandling;

import org.springframework.http.HttpStatus;

public class UnSupportedVehicleException extends CarParkingException{

    public UnSupportedVehicleException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public UnSupportedVehicleException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause, httpStatus);
    }
}
