package com.software.modsen.rideservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Ride{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long driverId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private BigDecimal price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

}
