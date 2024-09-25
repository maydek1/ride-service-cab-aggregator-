package com.software.modsen.rideservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessages {
    public static final String RIDE_NOT_FOUND = "Not found ride with id: '%s'";
    public static final String PASSENGER_NOT_FOUND = "Not found passenger with id: '%s'";
    public static final String DRIVER_NOT_FOUND = "Not found driver with id: '%s'";
    public static final String RIDE_TO_ACCEPT_NOT_FOUND = "Not found ride to accept";
}
