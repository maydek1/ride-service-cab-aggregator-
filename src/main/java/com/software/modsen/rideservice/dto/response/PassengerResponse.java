package com.software.modsen.rideservice.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PassengerResponse {
    private Long id;
    private String firstName;
    private String secondName;
    private String phone;
    private String email;
    private BigDecimal money;
}
