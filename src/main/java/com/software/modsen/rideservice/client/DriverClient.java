package com.software.modsen.rideservice.client;

import com.software.modsen.rideservice.config.FeignConfig;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import com.software.modsen.rideservice.exception.DriverNotFoundException;
import com.software.modsen.rideservice.exception.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.software.modsen.rideservice.util.ExceptionMessages.*;

@FeignClient(name = "${driver-service.name}", path = "${driver-service.path}" ,configuration = FeignConfig.class)
public interface DriverClient {
    @CircuitBreaker(name = "driverService", fallbackMethod = "handleUpdateRating")
    @PostMapping("/rating")
    ResponseEntity<DriverResponse> updateRating(@RequestBody DriverRatingRequest driverRatingRequest);

    @CircuitBreaker(name = "driverService", fallbackMethod = "handleToggleAvailable")
    @PostMapping("/available/{id}/{available}")
    ResponseEntity<DriverResponse> toggleAvailable(@PathVariable Long id, @PathVariable boolean available);

    default ResponseEntity<DriverResponse> handleUpdateRating(DriverRatingRequest driverRatingRequest, Throwable throwable) {
        if (throwable instanceof FeignException && ((FeignException) throwable).status() == 404) {
            throw new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, driverRatingRequest.getDriverId()));
        }
        if (throwable instanceof CallNotPermittedException) {
            throw new ServiceUnavailableException(String.format(CIRCUIT_BREAKER_IS_OPEN, "driver"));
        }
        throw new ServiceUnavailableException(String.format(SERVICE_UNAVAILABLE, "Driver"));
    }

    default ResponseEntity<DriverResponse> handleToggleAvailable(Long id, boolean available, Throwable throwable) {
        if (throwable instanceof FeignException && ((FeignException) throwable).status() == 404) {
            throw new DriverNotFoundException(String.format(DRIVER_NOT_FOUND, id));
        }

        if (throwable instanceof CallNotPermittedException) {
            throw new ServiceUnavailableException(String.format(CIRCUIT_BREAKER_IS_OPEN, "driver"));
        }

        throw new ServiceUnavailableException(String.format(SERVICE_UNAVAILABLE, "Driver"));
    }
}
