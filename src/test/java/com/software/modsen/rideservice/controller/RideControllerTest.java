package com.software.modsen.rideservice.controller;

import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.dto.response.RideResponseSet;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.service.DriverService;
import com.software.modsen.rideservice.service.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideControllerTest {

    @InjectMocks
    private RideController rideController;

    @Mock
    private RideService rideService;

    @Mock
    private DriverService driverService;

    @Mock
    private RideMapper rideMapper;

    private RideRequest rideRequest;
    private RideResponse rideResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rideRequest = new RideRequest();
        rideResponse = new RideResponse();
    }

    @Test
    void createRide_ShouldReturnCreatedRide() {
        when(rideMapper.rideRequestToRide(any(RideRequest.class))).thenReturn(new Ride());
        when(rideService.createRide(any(Ride.class))).thenReturn(new Ride());
        when(rideMapper.rideToRideResponse(any(Ride.class))).thenReturn(rideResponse);

        ResponseEntity<RideResponse> responseEntity = rideController.createRide(rideRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(rideResponse, responseEntity.getBody());
        verify(rideService, times(1)).createRide(any(Ride.class));
    }

    @Test
    void getRideById_ShouldReturnRide() {
        when(rideService.getRideById(anyLong())).thenReturn(new Ride());
        when(rideMapper.rideToRideResponse(any(Ride.class))).thenReturn(rideResponse);

        ResponseEntity<RideResponse> responseEntity = rideController.getRideById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rideResponse, responseEntity.getBody());
        verify(rideService, times(1)).getRideById(1L);
    }

    @Test
    void updateRide_ShouldReturnUpdatedRide() {
        when(rideMapper.rideRequestToRide(any(RideRequest.class))).thenReturn(new Ride());
        when(rideService.updateRide(anyLong(), any(Ride.class))).thenReturn(new Ride());
        when(rideMapper.rideToRideResponse(any(Ride.class))).thenReturn(rideResponse);

        ResponseEntity<RideResponse> responseEntity = rideController.updateRide(1L, rideRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rideResponse, responseEntity.getBody());
        verify(rideService, times(1)).updateRide(eq(1L), any(Ride.class));
    }

    @Test
    void deleteRideById_ShouldReturnDeletedRide() {
        when(rideService.deleteRideById(anyLong())).thenReturn(new Ride());
        when(rideMapper.rideToRideResponse(any(Ride.class))).thenReturn(rideResponse);

        ResponseEntity<RideResponse> responseEntity = rideController.deleteRideById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(rideResponse, responseEntity.getBody());
        verify(rideService, times(1)).deleteRideById(1L);
    }

    @Test
    void getAllRides_ShouldReturnRides() {
        when(rideService.getAllRides()).thenReturn(Collections.singleton(new Ride()));
        when(rideMapper.rideToRideResponse(any(Ride.class))).thenReturn(rideResponse);

        ResponseEntity<RideResponseSet> responseEntity = rideController.getAllRides();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).getRides().size());
        verify(rideService, times(1)).getAllRides();
    }
}
