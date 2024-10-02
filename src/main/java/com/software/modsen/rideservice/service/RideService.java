package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.client.PassengerClient;
import com.software.modsen.rideservice.dto.request.RideDriverRequest;
import com.software.modsen.rideservice.dto.request.RideStatusRequest;
import com.software.modsen.rideservice.dto.response.PassengerResponse;
import com.software.modsen.rideservice.exception.NotEnoughMoneyException;
import com.software.modsen.rideservice.exception.PassengerNotFoundException;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.software.modsen.rideservice.model.RideStatus.*;
import static com.software.modsen.rideservice.util.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverService driverService;
    private final PassengerClient passengerClient;
    private final DriverClient driverClient;

    public Ride getRideById(Long id) {
        return getOrElseThrow(id);
    }

    public Ride deleteRideById(Long id) {
        Ride rideResponse = getOrElseThrow(id);
        rideRepository.deleteById(id);
        return rideResponse;
    }

    public Ride updateRide(Long id, Ride ride) {
        Ride existingRide = rideRepository.findById(id)
                        .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));

        ride.setId(existingRide.getId());
        return rideRepository.save(ride);
    }

    public Ride createRide(Ride ride) {
        ride.setStatus(RideStatus.CREATED);
        rideRepository.save(ride);
        return ride;
    }

    public Set<Ride> getAllRides(){
        return new HashSet<>(rideRepository.findAll());
    }

    public Set<Ride> getRidesByDriver(Long driverId) {
        return rideRepository.findAllByDriverId(driverId);
    }

    public Set<Ride> getRidesByPassenger(Long passengerId) {
        return rideRepository.findAllByPassengerId(passengerId);
    }

    public Set<Ride> getRidesByStatus(RideStatus status){
        return rideRepository.findAllByStatus(status);
    }

    public Ride changeStatus(RideStatusRequest rideStatusRequest){
        Ride ride = rideRepository.findById(rideStatusRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, rideStatusRequest.getRideId())));
        ride.setStatus(rideStatusRequest.getStatus());
        return rideRepository.save(ride);
    }

    private Ride getOrElseThrow(Long id){
        return rideRepository.findById(id)
                .orElseThrow(()-> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));
    }

    public Ride setDriver(RideDriverRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, request.getRideId())));
        ride.setDriverId(request.getDriverId());
        return rideRepository.save(ride);
    }

    public Set<Ride> getRidesToConfirm(Long driverId) {
        return rideRepository.findAllByStatusAndDriverId(RideStatus.CREATED, driverId);
    }

    public Ride updateRideStatus(RideStatusRequest request) {
        Ride ride;

        switch (request.getStatus()) {
            case ACCEPTED -> {
                ride = findByStatusAndId(RideStatus.CREATED, request.getRideId());
                ride.setStartDate(LocalDateTime.now());
                ride.setStatus(ACCEPTED);
                driverClient.toggleAvailable(ride.getDriverId(), false);
            }
            case CANCELED -> {
                ride = findByStatusAndId(RideStatus.CREATED, request.getRideId());
                ride.setStatus(CANCELED);
                driverClient.toggleAvailable(ride.getDriverId(), true);
            }
            case COMPLETED -> {
                ride = findByStatusAndId(ACCEPTED, request.getRideId());
                ride.setEndDate(LocalDateTime.now());
                ride.setStatus(COMPLETED);
                driverClient.toggleAvailable(ride.getDriverId(), true);
            }
            default -> throw new IllegalArgumentException(String.format(INVALID_STATUS_REQUEST, request.getStatus()));
        }

        return rideRepository.save(ride);
    }

    public Ride findByStatusAndId(RideStatus status, Long id){
        return rideRepository.findByStatusAndId(status, id)
                .orElseThrow(() -> new RideNotFoundException(RIDE_NOT_FOUND));
    }

    @Transactional
    public Ride startRide(Ride ride) {
        ride.setStatus(CREATED);
        ride = rideRepository.save(ride);
        PassengerResponse passengerResponse = passengerClient.getPassengerById(ride.getPassengerId()).getBody();
        if (passengerResponse == null) throw new PassengerNotFoundException(String.format(PASSENGER_NOT_FOUND, ride.getPassengerId()));
        if (passengerResponse.getMoney().compareTo(ride.getPrice()) < 0) throw new NotEnoughMoneyException(NOT_ENOUGH_MONEY);
        Long driverId = driverService.findAvailableDriver();
        return setDriver(new RideDriverRequest(driverId, ride.getId()));
    }
}

