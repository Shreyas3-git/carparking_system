package com.demo.design.carparking.errorhandling;

import org.springframework.http.HttpStatus;

public class ReservationException extends CarParkingException
{

    public ReservationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public ReservationException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause, httpStatus);
    }
}
