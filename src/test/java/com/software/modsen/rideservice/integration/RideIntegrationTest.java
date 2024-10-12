package com.software.modsen.rideservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.modsen.rideservice.RideServiceApplication;
import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RideServiceApplication.class)
@AutoConfigureMockMvc
public class RideIntegrationTest extends DataBaseContainerConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        rideRepository.deleteAll();
    }

    private Ride createTestRide(Long driverId, Long passengerId, String pickupAddress, String destinationAddress, RideStatus status) {
        Ride ride = Ride.builder()
                .pickupAddress(pickupAddress)
                .destinationAddress(destinationAddress)
                .driverId(driverId)
                .passengerId(passengerId)
                .status(status)
                .build();
        return rideRepository.save(ride);
    }

    private RideRequest createTestRideRequest() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPickupAddress("Location A");
        rideRequest.setDestinationAddress("Location B");
        rideRequest.setDriverId(1L);
        rideRequest.setPassengerId(2L);
        rideRequest.setPrice(new BigDecimal("10.00"));
        return rideRequest;
    }

    @Test
    public void testCreateRide() throws Exception {
        RideRequest rideRequest = createTestRideRequest();

        mockMvc.perform(post("/api/v1/ride")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pickupAddress").value("Location A"))
                .andExpect(jsonPath("$.destinationAddress").value("Location B"));
    }

    @Test
    public void testGetRideById() throws Exception {
        Ride ride = createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);

        mockMvc.perform(get("/api/v1/ride/{id}", ride.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupAddress").value("Location A"))
                .andExpect(jsonPath("$.destinationAddress").value("Location B"));
    }

    @Test
    public void testUpdateRide() throws Exception {
        Ride ride = createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);

        RideRequest updateRequest = createTestRideRequest();
        updateRequest.setPickupAddress("Location C");
        updateRequest.setDestinationAddress("Location D");
        updateRequest.setPrice(new BigDecimal("15.00"));

        mockMvc.perform(put("/api/v1/ride/{id}", ride.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupAddress").value("Location C"))
                .andExpect(jsonPath("$.destinationAddress").value("Location D"));
    }

    @Test
    public void testDeleteRide() throws Exception {
        Ride ride = createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);

        mockMvc.perform(delete("/api/v1/ride/{id}", ride.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/ride/{id}", ride.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRides() throws Exception {
        createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);
        createTestRide(3L, 4L, "Location C", "Location D", RideStatus.CREATED);

        mockMvc.perform(get("/api/v1/ride"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.rides").isArray())
                .andExpect(jsonPath("$.rides.length()").value(2));
    }

    @Test
    public void testGetRidesByDriver() throws Exception {
        createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);

        mockMvc.perform(get("/api/v1/ride/driver/{driverId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides").isArray())
                .andExpect(jsonPath("$.rides.length()").value(1))
                .andExpect(jsonPath("$.rides[0].pickupAddress").value("Location A"));
    }

    @Test
    public void testGetRidesByPassenger() throws Exception {
        createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);

        mockMvc.perform(get("/api/v1/ride/passenger/{passengerId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides").isArray())
                .andExpect(jsonPath("$.rides.length()").value(1))
                .andExpect(jsonPath("$.rides[0].pickupAddress").value("Location A"));
    }

    @Test
    public void testGetRidesByStatus() throws Exception {
        createTestRide(1L, 2L, "Location A", "Location B", RideStatus.CREATED);

        mockMvc.perform(get("/api/v1/ride/status/{status}", RideStatus.CREATED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides").isArray())
                .andExpect(jsonPath("$.rides.length()").value(1))
                .andExpect(jsonPath("$.rides[0].pickupAddress").value("Location A"));
    }
}
