package com.software.modsen.rideservice.mapper;


import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.model.Ride;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RideMapper {
    RideResponse rideToRideResponse(Ride ride);

    Ride rideRequestToRide(RideRequest rideRequest);

}
