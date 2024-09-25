package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.dto.request.RideDriverRequest;
import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.dto.request.RideStatusRequest;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;

    public Ride getRideById(Long id) {
        return getOrElseThrow(id);
    }

    public Ride deleteRideById(Long id) {
        Ride rideResponse = getOrElseThrow(id);
        rideRepository.deleteById(id);
        return rideResponse;
    }

    public Ride updateRide(Long id, RideRequest rideRequest) {
        Ride ride = rideRepository.findById(id)
                        .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));

        Ride ride2 = rideMapper.rideRequestToRide(rideRequest);
        ride2.setId(ride.getId());
        return rideRepository.save(ride2);
    }

    public Ride createRide(RideRequest rideRequest) {
        Ride ride = rideMapper.rideRequestToRide(rideRequest);
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
        ride.setStatus(RideStatus.valueOf(rideStatusRequest.getStatus()));
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

    public Ride acceptRide(Long id) {
        Ride ride = findByStatusAndId(RideStatus.CREATED, id);

        ride.setStartDate(LocalDateTime.now());
        ride.setStatus(RideStatus.ACCEPTED);

        return rideRepository.save(ride);
    }

    public Ride rejectRide(Long id) {
        Ride ride = findByStatusAndId(RideStatus.CREATED, id);
        ride.setStatus(RideStatus.CANCELED);

        return rideRepository.save(ride);
    }

    public Ride completedRide(Long id) {
        Ride ride = findByStatusAndId(RideStatus.ACCEPTED, id);

        ride.setEndDate(LocalDateTime.now());
        ride.setStatus(RideStatus.COMPLETED);

        return rideRepository.save(ride);
    }

    public Ride findByStatusAndId(RideStatus status, Long id){
        return rideRepository.findByStatusAndId(status, id)
                .orElseThrow(() -> new RideNotFoundException(RIDE_NOT_FOUND));
    }
}

