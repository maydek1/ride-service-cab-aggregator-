package com.software.modsen.rideservice.controller;

import com.software.modsen.rideservice.dto.request.*;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.dto.response.RideResponseSet;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.service.DriverService;
import com.software.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ride")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final DriverService driverService;
    private final RideMapper rideMapper;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@RequestBody RideRequest rideRequest) {
        RideResponse createdRide = rideMapper.rideToRideResponse(rideService.createRide(
                rideMapper.rideRequestToRide(rideRequest)));
        return new ResponseEntity<>(createdRide, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long id) {
        RideResponse ride = rideMapper.rideToRideResponse(rideService.getRideById(id));
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable Long id, @RequestBody RideRequest rideRequest) {
        RideResponse updatedRide = rideMapper.rideToRideResponse(rideService.updateRide(id
                ,rideMapper.rideRequestToRide(rideRequest)));
        return new ResponseEntity<>(updatedRide, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RideResponse> deleteRideById(@PathVariable Long id) {
        RideResponse deletedRide = rideMapper.rideToRideResponse(rideService.deleteRideById(id));
        return new ResponseEntity<>(deletedRide, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<RideResponseSet> getAllRides() {
        RideResponseSet rides = new RideResponseSet(rideService.getAllRides().stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<RideResponseSet> getRidesByDriver(@PathVariable Long driverId) {
        RideResponseSet rides = new RideResponseSet(rideService.getRidesByDriver(driverId)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<RideResponseSet> getRidesByPassenger(@PathVariable Long passengerId) {
        RideResponseSet rides = new RideResponseSet(rideService.getRidesByPassenger(passengerId)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<RideResponseSet> getRidesByStatus(@PathVariable RideStatus status) {
        RideResponseSet rides = new RideResponseSet(rideService.getRidesByStatus(status)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @PutMapping("/status")
    public ResponseEntity<RideResponse> changeRideStatus(@RequestBody RideStatusRequest rideStatus) {
        RideResponse ride = rideMapper.rideToRideResponse(rideService.changeStatus(rideStatus));
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @PutMapping("/driver")
    public ResponseEntity<RideResponse> setDriver(@RequestBody RideDriverRequest request){
        RideResponse rideResponse = rideMapper.rideToRideResponse(rideService.setDriver(request));
        return ResponseEntity.ok(rideResponse);
    }

    @GetMapping("/created/{id}")
    public ResponseEntity<RideResponseSet> getRideToConfirm(@PathVariable Long id){
        return ResponseEntity.ok(new RideResponseSet(rideService.getRidesToConfirm(id)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet())));
    }

    @PostMapping("/update-status")
    public ResponseEntity<RideResponse> updateRideStatus(
            @RequestBody RideStatusRequest statusRequest) {

        RideResponse rideResponse = rideMapper.rideToRideResponse(rideService.updateRideStatus(statusRequest));
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/driver")
    ResponseEntity<DriverResponse> updateRating(@RequestBody DriverRatingRequest driverRatingRequest){
        return driverService.updateRating(driverRatingRequest);
    }

    @PostMapping("/ride")
    public ResponseEntity<RideResponse> startRide(@RequestBody RideRequest rideRequest){
        return ResponseEntity.ok().body(rideMapper.rideToRideResponse(rideService.startRide(
               rideMapper.rideRequestToRide(rideRequest))));
    }
}