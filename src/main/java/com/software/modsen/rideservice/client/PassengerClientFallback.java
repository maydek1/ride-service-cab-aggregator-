package com.software.modsen.rideservice.client;

import com.software.modsen.rideservice.dto.response.PassengerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PassengerClientFallback implements PassengerClient{
    @Override
    public ResponseEntity<PassengerResponse> getPassengerById(Long id) {
        PassengerResponse fallbackResponse = new PassengerResponse();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}
