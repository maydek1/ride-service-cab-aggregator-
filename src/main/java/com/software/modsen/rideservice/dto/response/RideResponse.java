package com.software.modsen.rideservice.dto.response;

import com.software.modsen.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideResponse {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private String pickup_address;
    private String destination_address;
    private BigDecimal price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RideStatus status;
}
