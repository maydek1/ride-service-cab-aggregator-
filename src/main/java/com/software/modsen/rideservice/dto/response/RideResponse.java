package com.software.modsen.rideservice.dto.response;

import com.software.modsen.rideservice.model.RideStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter
public class RideResponse {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private BigDecimal price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RideStatus status;
}
