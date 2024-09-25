package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverClient driverClient;
    private final RideRepository rideRepository;

    public ResponseEntity<DriverResponse> updateRating(DriverRatingRequest driverRatingRequest) {
        Long driverId = rideRepository.findById(driverRatingRequest.getRideId())
                .orElseThrow( () -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, driverRatingRequest.getRideId())))
                .getDriverId();
        driverRatingRequest.setDriverId(driverId);
        return driverClient.updateRating(driverRatingRequest);
    }
}
