package com.software.modsen.rideservice.controller;

import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.dto.response.RideResponseSet;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@RequestBody RideRequest rideRequest) {
        RideResponse createdRide = rideService.createRide(rideRequest);
        return new ResponseEntity<>(createdRide, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long id) {
        RideResponse ride = rideService.getRideById(id);
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable Long id, @RequestBody RideRequest rideRequest) {
        RideResponse updatedRide = rideService.updateRide(id, rideRequest);
        return new ResponseEntity<>(updatedRide, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RideResponse> deleteRideById(@PathVariable Long id) {
        RideResponse deletedRide = rideService.deleteRideById(id);
        return new ResponseEntity<>(deletedRide, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<RideResponseSet> getAllRides() {
        RideResponseSet rides = rideService.getAllRides();
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<RideResponseSet> getRidesByDriver(@PathVariable Long driverId) {
        RideResponseSet rides = rideService.getRidesByDriver(driverId);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<RideResponseSet> getRidesByPassenger(@PathVariable Long passengerId) {
        RideResponseSet rides = rideService.getRidesByPassenger(passengerId);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<RideResponseSet> getRidesByStatus(@PathVariable RideStatus status) {
        RideResponseSet rides = rideService.getRidesByStatus(status);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RideResponse> changeRideStatus(@PathVariable Long id, @RequestBody RideStatus rideStatus) {
        RideResponse ride = rideService.changeStatus(id, rideStatus);
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }
}