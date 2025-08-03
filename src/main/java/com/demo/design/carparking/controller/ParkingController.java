package com.demo.design.carparking.controller;

import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.ParkingSpot;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.Vehicle;
import com.demo.design.carparking.service.CheckInService;
import com.demo.design.carparking.service.CheckoutService;
import com.demo.design.carparking.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/api/parking")
public class ParkingController
{
    private final CheckInService checkInService;
    private final CheckoutService checkoutService;
    private final ReservationService reservationService;

    public ParkingController(
            CheckInService checkInService,
            CheckoutService checkoutService,
            ReservationService reservationService) {
        this.checkInService = checkInService;
        this.checkoutService = checkoutService;
        this.reservationService = reservationService;
    }
    @PostMapping("/check-in")
    public ResponseEntity<CommonResponse> checkIn(@Valid @RequestBody Vehicle request,Long floor) {
        return checkInService.checkIn(request,floor);
    }

    @PostMapping("/check-out/{licensePlate}")
    public ResponseEntity<CommonResponse> checkOut(@PathVariable String licensePlate) {
        try {
            return checkoutService.checkOut(licensePlate, LocalDateTime.now());
        } catch (IllegalStateException e) {
            CommonResponse res = new CommonResponse.CommonResponseBuilder()
                    .message(e.getMessage())
                    .status("Failed")
                    .errorCode("400")
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping("/available-spots/{type}")
    public ResponseEntity<Long> getAvailableSpots(@PathVariable ParkingSpotType type) {
        return ResponseEntity.ok(checkInService.getAvailableSpotsCount(type));
    }

    @PostMapping("/reserve")
    public ResponseEntity<CommonResponse> reserveSpot(@Valid @RequestBody Vehicle request, @RequestParam Long floorId) {
        return reservationService.reserveSpot(request, floorId);
    }

    @PostMapping("/cancel-reservation/{licensePlate}")
    public ResponseEntity<CommonResponse> cancelReservation(@PathVariable String licensePlate) {
        return reservationService.cancelReservation(licensePlate);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonResponse> methofArgumentNotValid(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.append(fieldName).append(":").append(message).append(";");
        });
        CommonResponse response = new CommonResponse.CommonResponseBuilder()
                .message(errorMessage.toString())
                .errorCode("400")
                .status("Failed")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

}
