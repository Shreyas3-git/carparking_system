package com.demo.design.carparking.controller;


import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.demo.design.carparking.beanconfig.ParkingConfig;
import com.demo.design.carparking.service.CheckInService;
import com.demo.design.carparking.service.CheckoutService;
import com.demo.design.carparking.service.ReservationService;
import com.demo.design.carparking.dto.CommonResponse;
import com.demo.design.carparking.entity.ParkingSpotType;
import com.demo.design.carparking.entity.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ParkingController.class)
@ImportAutoConfiguration(ParkingConfig.class)
class ParkingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckInService checkInService;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @DisplayName("checkIn_ShouldReturnSuccess_WhenValidRequest")
    void checkIn_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        CommonResponse expectedResponse = createSuccessResponse("Check-in successful");
        when(checkInService.checkIn(any(Vehicle.class), anyLong()))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        mockMvc.perform(post("/api/parking/check-in")
                        .param("floor", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createValidVehicleJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Check-in successful"));
    }

    @Test
    @DisplayName("checkIn_ShouldReturnBadRequest_WhenInvalidVehicleData")
    void checkIn_ShouldReturnBadRequest_WhenInvalidVehicleData() throws Exception {
        mockMvc.perform(post("/api/parking/check-in")
                        .param("floor", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createInvalidVehicleJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Failed"));
    }

    @Test
    @DisplayName("checkOut_ShouldReturnSuccess_WhenValidLicensePlate")
    void checkOut_ShouldReturnSuccess_WhenValidLicensePlate() throws Exception {
        String licensePlate = "ABC123";
        CommonResponse expectedResponse = createSuccessResponse("Checkout successful. Fee: $10.0");
        when(checkoutService.checkOut(eq(licensePlate), any(LocalDateTime.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        mockMvc.perform(post("/api/parking/check-out/{licensePlate}", licensePlate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Checkout successful. Fee: $10.0"));
    }

    @Test
    @DisplayName("getAvailableSpots_ShouldReturnCount_WhenValidSpotType")
    void getAvailableSpots_ShouldReturnCount_WhenValidSpotType() throws Exception {
        when(checkInService.getAvailableSpotsCount(ParkingSpotType.MEDIUM))
                .thenReturn(15L);

        mockMvc.perform(get("/api/parking/available-spots/{type}", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    @DisplayName("reserveSpot_ShouldReturnSuccess_WhenValidRequest")
    void reserveSpot_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        CommonResponse expectedResponse = createSuccessResponse("Reservation successful");
        when(reservationService.reserveSpot(any(Vehicle.class), anyLong()))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        mockMvc.perform(post("/api/parking/reserve")
                        .param("floorId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createValidVehicleJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Reservation successful"));
    }

    @Test
    @DisplayName("cancelReservation_ShouldReturnSuccess_WhenValidLicensePlate")
    void cancelReservation_ShouldReturnSuccess_WhenValidLicensePlate() throws Exception {
        String licensePlate = "ABC123";
        CommonResponse expectedResponse = createSuccessResponse("Reservation cancelled");
        when(reservationService.cancelReservation(eq(licensePlate)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        mockMvc.perform(post("/api/parking/cancel-reservation/{licensePlate}", licensePlate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Reservation cancelled"));
    }

    private String createValidVehicleJson() {
        return """
            {
                "licensePlate": "ABC123",
                "type": "CAR",
                "entryTime": "2024-01-01T10:00:00"
            }
            """;
    }

    private String createInvalidVehicleJson() {
        return """
            {
                "licensePlate": "",
                "type": null,
                "entryTime": null
            }
            """;
    }

    private CommonResponse createSuccessResponse(String message) {
        return new CommonResponse.CommonResponseBuilder()
                .message(message)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
