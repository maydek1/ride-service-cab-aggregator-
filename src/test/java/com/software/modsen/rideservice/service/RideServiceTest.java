package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.client.PassengerClient;
import com.software.modsen.rideservice.dto.request.ChargeMoneyRequest;
import com.software.modsen.rideservice.dto.request.RideStatusRequest;
import com.software.modsen.rideservice.dto.response.PassengerResponse;
import com.software.modsen.rideservice.exception.NotEnoughMoneyException;
import com.software.modsen.rideservice.exception.PassengerNotFoundException;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.software.modsen.rideservice.model.RideStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private DriverService driverService;
    @Mock
    private PassengerClient passengerClient;
    @Mock
    private DriverClient driverClient;

    @InjectMocks
    private RideService rideService;

    private Ride ride;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ride = new Ride();
        ride.setId(1L);
        ride.setPrice(BigDecimal.valueOf(100));
        ride.setPassengerId(1L);
        ride.setDriverId(2L);
        ride.setStatus(CREATED);
    }

    @Test
    void getRideById_ShouldReturnRide_WhenRideExists() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        Ride result = rideService.getRideById(1L);
        assertEquals(ride, result);
        verify(rideRepository).findById(1L);
    }

    @Test
    void getRideById_ShouldThrowException_WhenRideNotFound() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RideNotFoundException.class, () -> rideService.getRideById(1L));
    }

    @Test
    void deleteRideById_ShouldDeleteRide_WhenRideExists() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        rideService.deleteRideById(1L);
        verify(rideRepository).deleteById(1L);
    }

    @Test
    void deleteRideById_ShouldThrowException_WhenRideNotFound() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RideNotFoundException.class, () -> rideService.deleteRideById(1L));
    }

    @Test
    void updateRide_ShouldUpdateRide_WhenRideExists() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride updatedRide = rideService.updateRide(1L, ride);
        assertEquals(ride, updatedRide);
    }

    @Test
    void updateRide_ShouldThrowException_WhenRideNotFound() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RideNotFoundException.class, () -> rideService.updateRide(1L, ride));
    }

    @Test
    void createRide_ShouldCreateRide() {
        when(rideRepository.save(ride)).thenReturn(ride);
        Ride createdRide = rideService.createRide(ride);
        assertEquals(CREATED, createdRide.getStatus());
    }

    @Test
    void getAllRides_ShouldReturnSetOfRides() {
        when(rideRepository.findAll()).thenReturn(List.of(ride));
        Set<Ride> rides = rideService.getAllRides();
        assertTrue(rides.contains(ride));
    }

    @Test
    void changeStatus_ShouldChangeRideStatus() {
        RideStatusRequest request = new RideStatusRequest(COMPLETED, 1L);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride updatedRide = rideService.changeStatus(request);
        assertEquals(COMPLETED, updatedRide.getStatus());
    }

    @Test
    void updateRideStatusToCompleted_ShouldHandleCompletedStatus() {
        RideStatusRequest request = new RideStatusRequest(COMPLETED, 1L);
        when(rideRepository.findByStatusAndId(ACCEPTED, 1L)).thenReturn(Optional.of(ride));
        when(passengerClient.chargeMoney(any(ChargeMoneyRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride updatedRide = rideService.updateRideStatus(request);
        assertEquals(COMPLETED, updatedRide.getStatus());
    }

    @Test
    void updateRideStatusToAccepted_ShouldHandleCompletedStatus() {
        RideStatusRequest request = new RideStatusRequest(ACCEPTED, 1L);
        when(rideRepository.findByStatusAndId(CREATED, 1L)).thenReturn(Optional.of(ride));
        when(passengerClient.chargeMoney(any(ChargeMoneyRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride updatedRide = rideService.updateRideStatus(request);
        assertEquals(ACCEPTED, updatedRide.getStatus());
    }

    @Test
    void updateRideStatus_ShouldThrowException_WhenStatusNotExist() {
        RideStatusRequest request = new RideStatusRequest(CREATED, 1L);

        assertThrows(IllegalArgumentException.class, () -> rideService.updateRideStatus(request));
    }

    @Test
    void updateRideStatusToCanceled_ShouldHandleCompletedStatus() {
        RideStatusRequest request = new RideStatusRequest(CANCELED, 1L);
        when(rideRepository.findByStatusAndId(CREATED, 1L)).thenReturn(Optional.of(ride));
        when(passengerClient.chargeMoney(any(ChargeMoneyRequest.class))).thenReturn(ResponseEntity.ok().build());
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride updatedRide = rideService.updateRideStatus(request);
        assertEquals(CANCELED, updatedRide.getStatus());
    }

    @Test
    void startRide_ShouldStartRide_WhenPassengerHasEnoughMoney() {
        PassengerResponse passengerResponse = new PassengerResponse();
        passengerResponse.setMoney(BigDecimal.valueOf(200));
        when(passengerClient.getPassengerById(ride.getPassengerId())).thenReturn(ResponseEntity.ok(passengerResponse));
        when(driverService.findAvailableDriver()).thenReturn(2L);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        Ride startedRide = rideService.startRide(ride);
        assertEquals(2L, startedRide.getDriverId());
    }

    @Test
    void startRide_ShouldThrowException_WhenNotEnoughMoney() {
        PassengerResponse passengerResponse = new PassengerResponse();
        passengerResponse.setMoney(BigDecimal.valueOf(50));
        when(rideRepository.save(ride)).thenReturn(ride);
        when(passengerClient.getPassengerById(ride.getPassengerId())).thenReturn(ResponseEntity.ok(passengerResponse));

        assertThrows(NotEnoughMoneyException.class, () -> rideService.startRide(ride));
    }

    @Test
    void startRide_ShouldThrowException_WhenPassengerNotFound() {
        when(rideRepository.save(ride)).thenReturn(ride);
        when(passengerClient.getPassengerById(any())).thenReturn(ResponseEntity.ok(null));
        assertThrows(PassengerNotFoundException.class, () -> rideService.startRide(ride));
    }

    @Test
    void getRidesToConfirm_ShouldReturnSetOfRides(){
        Long driverId = 2L;

        when(rideRepository.findAllByStatusAndDriverId(RideStatus.CREATED, driverId)).thenReturn(Set.of(ride));
        Set<Ride> rides = rideService.getRidesToConfirm(2L);
        Iterator<Ride> iterator = rides.iterator();
        Ride ride1 = iterator.next();
        assertEquals(rides.size(), 1);
        assertEquals(ride1.getDriverId(), 2);
    }
}
