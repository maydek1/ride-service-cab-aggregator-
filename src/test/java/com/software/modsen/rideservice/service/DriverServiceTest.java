package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import com.software.modsen.rideservice.exception.DriverNotFoundException;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.repositories.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static com.software.modsen.rideservice.util.ExceptionMessages.AVAILABLE_DRIVER_NOT_FOUND;
import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @InjectMocks
    private DriverService driverService;
    @Mock
    private DriverClient driverClient;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Queue responseQueue;

    @Mock
    private Queue requestQueue;

    private Ride ride;
    private DriverRatingRequest driverRatingRequest;

    @BeforeEach
    void setUp() {
        ride = new Ride();
        ride.setId(1L);
        ride.setDriverId(1L);

        driverRatingRequest = new DriverRatingRequest();
        driverRatingRequest.setRideId(ride.getId());
        driverRatingRequest.setRate(5);
    }

    @Test
    void updateRating_ShouldUpdateDriverRating_WhenRideExists() {
        when(rideRepository.findById(driverRatingRequest.getRideId())).thenReturn(Optional.of(ride));
        ResponseEntity<DriverResponse> response = ResponseEntity.ok(new DriverResponse());
        when(driverClient.updateRating(driverRatingRequest)).thenReturn(response);

        ResponseEntity<DriverResponse> result = driverService.updateRating(driverRatingRequest);

        assertEquals(response, result);
        verify(driverClient, times(1)).updateRating(driverRatingRequest);
    }

    @Test
    void updateRating_ShouldThrowRideNotFoundException_WhenRideDoesNotExist() {
        when(rideRepository.findById(driverRatingRequest.getRideId())).thenReturn(Optional.empty());

        RideNotFoundException exception = assertThrows(RideNotFoundException.class, () -> driverService.updateRating(driverRatingRequest));
        assertEquals(String.format(RIDE_NOT_FOUND, driverRatingRequest.getRideId()), exception.getMessage());
    }

    @Test
    void findAvailableDriver_ShouldReturnDriverId_WhenDriverIsAvailable() {
        Message responseMessage = new Message("1".getBytes());
        when(responseQueue.getName()).thenReturn("queue");

        when(rabbitTemplate.receive(anyString(), anyLong())).thenReturn(responseMessage);

        long driverId = driverService.findAvailableDriver();

        assertEquals(1L, driverId);
    }

    @Test
    void findAvailableDriver_ShouldThrowDriverNotFoundException_WhenNoDriverIsAvailable() {
        when(rabbitTemplate.receive(anyString(), anyLong())).thenReturn(null);
        when(responseQueue.getName()).thenReturn("queue");

        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class, () -> driverService.findAvailableDriver());
        assertEquals(AVAILABLE_DRIVER_NOT_FOUND, exception.getMessage());
    }
}
