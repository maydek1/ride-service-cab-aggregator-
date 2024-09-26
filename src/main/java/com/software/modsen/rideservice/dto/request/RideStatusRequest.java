package com.software.modsen.rideservice.dto.request;

import com.software.modsen.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideStatusRequest {
    private RideStatus status;
    private Long rideId;
}
