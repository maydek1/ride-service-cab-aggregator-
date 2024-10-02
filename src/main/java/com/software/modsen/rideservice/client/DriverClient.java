package com.software.modsen.rideservice.client;

import com.software.modsen.rideservice.config.FeignConfig;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${driver-service.name}", path = "${driver-service.path}" ,configuration = FeignConfig.class, fallback = DriverClientFallback.class)
public interface DriverClient {
    @CircuitBreaker(name = "driverService")
    @PostMapping("/rating")
    ResponseEntity<DriverResponse> updateRating(@RequestBody DriverRatingRequest driverRatingRequest);

    @CircuitBreaker(name = "driverService")
    @PostMapping("/available/{id}/{available}")
    ResponseEntity<DriverResponse> toggleAvailable(@PathVariable Long id, @PathVariable boolean available);
}
