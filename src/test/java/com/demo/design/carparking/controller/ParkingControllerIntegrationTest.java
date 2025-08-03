//package com.demo.design.carparking.controller;
//
//
//import com.demo.design.carparking.entity.ParkingSpot;
//import com.demo.design.carparking.entity.ParkingSpotType;
//import com.demo.design.carparking.entity.Vehicle;
//import com.demo.design.carparking.entity.VehicleType;
//import com.demo.design.carparking.service.CheckInService;
//import com.demo.design.carparking.service.CheckoutService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = ParkingControllerIntegrationTest.class)
//@ActiveProfiles("test")
//class ParkingControllerIntegrationTest {
//
//    @Mock
//    private CheckInService checkInService;
//    @Mock
//    private CheckoutService checkoutService;
//
//    @InjectMocks
//    private ParkingController parkingController;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(parkingController).build();
//    }
//
//    @Test
//    public void testCheckIn_Success() throws Exception {
//        Vehicle vehicle = new Vehicle.VehicleBuilder()
//                .licensePlate("ABC123")
//                .type(VehicleType.CAR)
//                .entryTime(LocalDateTime.now())
//                .build();
//        ParkingSpot spot = new ParkingSpot.ParkingSpotBuilder()
//                .type(ParkingSpotType.MEDIUM)
//                .occupied(true)
//                .vehicle(vehicle)
//                .build();
//        when(checkInService.checkIn(any(Vehicle.class))).thenReturn(Optional.of(spot));
//
//        mockMvc.perform(post("/api/parking/check-in")
//                        .contentType("application/json")
//                        .content("{\"licensePlate\":\"ABC123\",\"type\":\"CAR\",\"entryTime\":\"" + LocalDateTime.now() + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Assigned spot: " + spot.getId()));
//    }
//
//    @Test
//    public void testCheckIn_NoAvailableSpot() throws Exception {
//        Vehicle vehicle = new Vehicle.VehicleBuilder()
//                .licensePlate("ABC123")
//                .type(VehicleType.CAR)
//                .entryTime(LocalDateTime.now())
//                .build();
//        when(checkInService.checkIn(any(Vehicle.class))).thenReturn(Optional.empty());
//
//        mockMvc.perform(post("/api/parking/check-in")
//                        .contentType("application/json")
//                        .content("{\"licensePlate\":\"ABC123\",\"type\":\"CAR\",\"entryTime\":\"" + LocalDateTime.now() + "\"}"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("No available spot"));
//    }
//
//    @Test
//    public void testCheckIn_InvalidVehicle() throws Exception {
//        mockMvc.perform(post("/api/parking/check-in")
//                        .contentType("application/json")
//                        .content("{\"licensePlate\":\"\",\"type\":\"CAR\",\"entryTime\":\"" + LocalDateTime.now() + "\"}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void testCheckOut_Success() throws Exception {
//        when(checkoutService.checkOut(eq("ABC123"), any(LocalDateTime.class))).thenReturn(10.0);
//
//        mockMvc.perform(post("/api/parking/check-out/ABC123")
//                        .contentType("application/json"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Parking fee: $10.0"));
//    }
//
//    @Test
//    public void testCheckOut_VehicleNotFound() throws Exception {
//        when(checkoutService.checkOut(eq("NONEXISTENT"), any(LocalDateTime.class)))
//                .thenThrow(new IllegalStateException("Vehicle not found"));
//
//        mockMvc.perform(post("/api/parking/check-out/NONEXISTENT")
//                        .contentType("application/json"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Vehicle not found"));
//    }
//
//    @Test
//    public void testGetAvailableSpots_Success() throws Exception {
//        when(checkInService.getAvailableSpotsCount(ParkingSpotType.MEDIUM)).thenReturn(1L);
//
//        mockMvc.perform(get("/api/parking/available-spots/MEDIUM"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("1"));
//    }
//}