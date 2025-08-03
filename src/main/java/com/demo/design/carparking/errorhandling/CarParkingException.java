package com.demo.design.carparking.errorhandling;

import org.springframework.http.HttpStatus;

public abstract class CarParkingException extends RuntimeException
{
    private final HttpStatus httpStatus;

    public CarParkingException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public CarParkingException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
