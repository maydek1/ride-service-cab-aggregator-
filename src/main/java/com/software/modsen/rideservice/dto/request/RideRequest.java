package com.software.modsen.rideservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideRequest {
    private Long driverId;
    private Long passengerId;
    private String pickup_address;
    private BigDecimal price;
}
