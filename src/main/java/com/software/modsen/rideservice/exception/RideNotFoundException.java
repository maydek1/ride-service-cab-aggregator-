package com.software.modsen.rideservice.exception;

public class RideNotFoundException extends RuntimeException{
    public RideNotFoundException(String msg){
        super(msg);
    }
}