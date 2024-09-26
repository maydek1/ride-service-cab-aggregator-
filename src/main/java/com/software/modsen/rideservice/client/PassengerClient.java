package com.software.modsen.rideservice.client;


import com.software.modsen.rideservice.config.FeignConfig;
import com.software.modsen.rideservice.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${passenger-service.name}", path = "${passenger-service.path}", configuration = FeignConfig.class)
public interface PassengerClient {
    @GetMapping("/{id}")
    ResponseEntity<PassengerResponse> getPassengerById(@PathVariable Long id);
}
