package com.software.modsen.rideservice.client;

import com.software.modsen.rideservice.config.FeignConfig;
import com.software.modsen.rideservice.dto.request.ChargeMoneyRequest;
import com.software.modsen.rideservice.dto.response.PassengerResponse;
import com.software.modsen.rideservice.exception.DriverNotFoundException;
import com.software.modsen.rideservice.exception.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.software.modsen.rideservice.util.ExceptionMessages.*;

@FeignClient(name = "${passenger-service.name}", path = "${passenger-service.path}", configuration = FeignConfig.class)
public interface PassengerClient {
    @CircuitBreaker(name = "passengerService")
    @GetMapping("/{id}")
    ResponseEntity<PassengerResponse> getPassengerById(@PathVariable Long id);

    @CircuitBreaker(name = "passengerService")
    @PostMapping("/money")
    ResponseEntity<PassengerResponse> chargeMoney(@RequestBody ChargeMoneyRequest chargeMoneyRequest);

    default ResponseEntity<PassengerResponse> handleGetPassengerById(Long id, Throwable throwable) {
        if (throwable instanceof FeignException && ((FeignException) throwable).status() == 404) {
            throw new DriverNotFoundException(String.format(PASSENGER_NOT_FOUND, id));
        }
        if (throwable instanceof CallNotPermittedException) {
            throw new ServiceUnavailableException(String.format(CIRCUIT_BREAKER_IS_OPEN, "passenger"));
        }
        throw new ServiceUnavailableException(String.format(SERVICE_UNAVAILABLE, "Passenger"));
    }

    default ResponseEntity<PassengerResponse> handleChargeMoney(ChargeMoneyRequest chargeMoneyRequest, Throwable throwable) {
        if (throwable instanceof FeignException && ((FeignException) throwable).status() == 404) {
            throw new DriverNotFoundException(String.format(PASSENGER_NOT_FOUND, chargeMoneyRequest.getPassengerId()));
        }

        if (throwable instanceof CallNotPermittedException) {
            throw new ServiceUnavailableException(String.format(CIRCUIT_BREAKER_IS_OPEN, "passenger"));
        }

        throw new ServiceUnavailableException(String.format(SERVICE_UNAVAILABLE, "Passenger"));
    }

}
