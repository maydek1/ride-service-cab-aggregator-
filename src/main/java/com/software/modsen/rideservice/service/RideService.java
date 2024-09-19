package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.dto.response.RideResponseSet;
import com.software.modsen.rideservice.model.RideStatus;

public interface RideService {
    RideResponse getRideById(Long id);
    RideResponse deleteRideById(Long id);
    RideResponse updateRide(Long id, RideRequest passengerRequest);
    RideResponse createRide(RideRequest passengerRequest);

    RideResponseSet getAllRides();

    RideResponseSet getRidesByDriver(Long driverId);
    RideResponseSet getRidesByPassenger(Long passengerId);

    RideResponse changeStatus(Long id, RideStatus rideStatus);

    RideResponseSet getRidesByStatus(RideStatus status);
}
