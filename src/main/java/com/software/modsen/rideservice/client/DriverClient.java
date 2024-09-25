package com.software.modsen.rideservice.client;


import com.software.modsen.rideservice.config.FeignConfig;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${driver-service.name}", url = "${driver-service.url}", configuration = FeignConfig.class)
public interface DriverClient {

    @PostMapping("/driver/rating")
    ResponseEntity<DriverResponse> updateRating(@RequestBody DriverRatingRequest driverRatingRequest);
}
