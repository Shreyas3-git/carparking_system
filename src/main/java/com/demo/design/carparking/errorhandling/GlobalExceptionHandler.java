package com.demo.design.carparking.errorhandling;

import com.demo.design.carparking.dto.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CommonResponse> handleNotFoundException(NotFoundException ex) {
        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message(ex.getMessage())
                .errorCode("NOT_FOUND")
                .status("FAIL")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ParkingSpotUnavailableException.class)
    public ResponseEntity<CommonResponse> handleParkingSpotUnavailable(ParkingSpotUnavailableException ex) {
        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message(ex.getMessage())
                .errorCode("SPOT_UNAVAILABLE")
                .status("FAIL")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleAllExceptions(Exception ex) {
        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message("An unexpected error occurred: " + ex.getMessage())
                .status("FAIL")
                .errorCode("INTERNAL_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
