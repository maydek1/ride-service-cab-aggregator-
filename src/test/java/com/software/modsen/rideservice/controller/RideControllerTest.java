package com.software.modsen.rideservice.controller;

import com.software.modsen.rideservice.dto.request.RideDriverRequest;
import com.software.modsen.rideservice.dto.request.RideStatusRequest;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.service.DriverService;
import com.software.modsen.rideservice.service.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RideControllerTest {

    @Mock
    private RideService rideService;

    @Mock
    private DriverService driverService;

    @Mock
    private RideMapper rideMapper;

    @InjectMocks
    private RideController rideController;

    private MockMvc mockMvc;

    private Ride ride;
    private RideResponse rideResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();

        ride = new Ride();
        ride.setId(1L);
        ride.setStatus(RideStatus.CREATED);

        rideResponse = new RideResponse();
        rideResponse.setId(1L);
        rideResponse.setStatus(RideStatus.CREATED);
    }

    @Test
    void createRide_ShouldReturnCreatedRide_WhenValidRequest() throws Exception {
        when(rideService.createRide(any(Ride.class))).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);
        when(rideMapper.rideRequestToRide(any())).thenReturn(ride);
        mockMvc.perform(post("/api/v1/ride")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passengerId\": 123, \"driverId\": 456}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).createRide(any(Ride.class));
    }

    @Test
    void getRideById_ShouldReturnRideResponse_WhenRideExists() throws Exception {
        when(rideService.getRideById(1L)).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(get("/api/v1/ride/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).getRideById(1L);
    }

    @Test
    void updateRide_ShouldReturnUpdatedRide_WhenValidRequest() throws Exception {
        when(rideService.updateRide(eq(1L), any(Ride.class))).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);
        when(rideMapper.rideRequestToRide(any())).thenReturn(ride);
        mockMvc.perform(put("/api/v1/ride/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passengerId\": 123, \"driverId\": 456}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).updateRide(eq(1L), any(Ride.class));
    }

    @Test
    void deleteRideById_ShouldReturnDeletedRide_WhenRideExists() throws Exception {
        when(rideService.deleteRideById(1L)).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(delete("/api/v1/ride/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).deleteRideById(1L);
    }

    @Test
    void getAllRides_ShouldReturnSetOfRides() throws Exception {
        when(rideService.getAllRides()).thenReturn(Set.of(ride));
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(get("/api/v1/ride"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides[0].id").value(1L))
                .andExpect(jsonPath("$.rides[0].status").value("CREATED"));

        verify(rideService, times(1)).getAllRides();
    }

    @Test
    void getRidesByDriver_ShouldReturnRides_WhenValidDriverId() throws Exception {
        when(rideService.getRidesByDriver(1L)).thenReturn(Set.of(ride));
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(get("/api/v1/ride/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides[0].id").value(1L))
                .andExpect(jsonPath("$.rides[0].status").value("CREATED"));

        verify(rideService, times(1)).getRidesByDriver(1L);
    }

    @Test
    void getRidesByPassenger_ShouldReturnRides_WhenValidPassengerId() throws Exception {
        when(rideService.getRidesByPassenger(1L)).thenReturn(Set.of(ride));
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(get("/api/v1/ride/passenger/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides[0].id").value(1L))
                .andExpect(jsonPath("$.rides[0].status").value("CREATED"));

        verify(rideService, times(1)).getRidesByPassenger(1L);
    }

    @Test
    void getRidesByStatus_ShouldReturnRides_WhenValidStatus() throws Exception {
        when(rideService.getRidesByStatus(RideStatus.CREATED)).thenReturn(Set.of(ride));
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(get("/api/v1/ride/status/CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides[0].id").value(1L))
                .andExpect(jsonPath("$.rides[0].status").value("CREATED"));

        verify(rideService, times(1)).getRidesByStatus(RideStatus.CREATED);
    }

    @Test
    void changeRideStatus_ShouldReturnUpdatedStatus_WhenValidRequest() throws Exception {
        when(rideService.changeStatus(any(RideStatusRequest.class))).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(put("/api/v1/ride/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rideId\": 1, \"status\": \"ACCEPTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).changeStatus(any(RideStatusRequest.class));
    }

    @Test
    void setDriver_ShouldReturnUpdatedRide_WhenValidRequest() throws Exception {
        when(rideService.setDriver(any(RideDriverRequest.class))).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(put("/api/v1/ride/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rideId\": 1, \"driverId\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).setDriver(any(RideDriverRequest.class));
    }

    @Test
    void getRideToConfirm_ShouldReturnRidesToConfirm_WhenValidId() throws Exception {
        when(rideService.getRidesToConfirm(1L)).thenReturn(Set.of(ride));
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);

        mockMvc.perform(get("/api/v1/ride/created/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rides[0].id").value(1L))
                .andExpect(jsonPath("$.rides[0].status").value("CREATED"));

        verify(rideService, times(1)).getRidesToConfirm(1L);
    }

    @Test
    void updateRideStatus_ShouldReturnUpdatedRideStatus_WhenValidRequest() throws Exception {
        when(rideService.updateRideStatus(any(RideStatusRequest.class))).thenReturn(ride);
        when(rideMapper.rideToRideResponse(ride)).thenReturn(rideResponse);
        when(rideMapper.rideRequestToRide(any())).thenReturn(ride);

        mockMvc.perform(post("/api/v1/ride/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rideId\": 1, \"status\": \"ACCEPTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));

        verify(rideService, times(1)).updateRideStatus(any(RideStatusRequest.class));
    }
}
