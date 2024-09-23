package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Ride changeStatus(Long id, RideStatus rideStatus){
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));
        ride.setStatus(rideStatus);

        return rideRepository.save(ride);
    }

    private Ride getOrElseThrow(Long id){
        return rideRepository.findById(id)
                .orElseThrow(()-> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));
    }

}
