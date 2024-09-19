package com.software.modsen.rideservice.service.ServiceImpl;

import com.software.modsen.rideservice.dto.request.RideRequest;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.dto.response.RideResponseSet;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import com.software.modsen.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideMapper rideMapper;

    @Override
    public RideResponse getRideById(Long id) {
        return getOrElseThrow(id);
    }

    @Override
    public RideResponse deleteRideById(Long id) {
        RideResponse rideResponse = getOrElseThrow(id);
        rideRepository.deleteById(id);
        return rideResponse;
    }

    @Override
    public RideResponse updateRide(Long id, RideRequest rideRequest) {
        Ride ride = rideRepository.findById(id)
                        .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));

        Ride ride2 = rideMapper.rideRequestToRide(rideRequest);
        ride2.setId(ride.getId());
        return rideMapper.rideToRideResponse(rideRepository.save(ride2));
    }

    @Override
    public RideResponse createRide(RideRequest rideRequest) {
        Ride ride = rideMapper.rideRequestToRide(rideRequest);
        rideRepository.save(ride);
        return rideMapper.rideToRideResponse(ride);
    }

    @Override
    public RideResponseSet getAllRides(){
        return new RideResponseSet(rideRepository.findAll()
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
    }

    @Override
    public RideResponseSet getRidesByDriver(Long driverId) {
        return toResponseSet(rideRepository.findAllByDriverId(driverId));
    }

    @Override
    public RideResponseSet getRidesByPassenger(Long passengerId) {
        return toResponseSet(rideRepository.findAllByPassengerId(passengerId));
    }

    @Override
    public RideResponseSet getRidesByStatus(RideStatus status){
        return toResponseSet(rideRepository.findAllByStatus(status));
    }

    @Override
    public RideResponse changeStatus(Long id, RideStatus rideStatus){
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id)));
        ride.setStatus(rideStatus);

        return rideMapper.rideToRideResponse(rideRepository.save(ride));
    }

    private RideResponse getOrElseThrow(Long id){
        return rideMapper.rideToRideResponse(rideRepository.findById(id)
                .orElseThrow(()-> new RideNotFoundException(String.format(RIDE_NOT_FOUND, id))));
    }

    private RideResponseSet toResponseSet(Set<Ride> rides){
        return new RideResponseSet(rides
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
    }
}
