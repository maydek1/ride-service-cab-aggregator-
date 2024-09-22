package com.software.modsen.rideservice.repositories;

import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Set<Ride> findAllByStatus(RideStatus status);
    Set<Ride> findAllByDriverId(Long driverId);
    Set<Ride> findAllByPassengerId(Long passengerId);

}
