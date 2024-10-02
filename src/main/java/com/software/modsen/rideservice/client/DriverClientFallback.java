package com.software.modsen.rideservice.client;

import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DriverClientFallback implements DriverClient{
    @Override
    public ResponseEntity<DriverResponse> updateRating(DriverRatingRequest driverRatingRequest) {
        DriverResponse fallbackResponse = new DriverResponse();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }

    @Override
    public ResponseEntity<DriverResponse> toggleAvailable(Long id, boolean available) {
        DriverResponse fallbackResponse = new DriverResponse();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}
