package com.software.modsen.rideservice.exception;

public class DriverNotFoundException extends RuntimeException{
    public DriverNotFoundException(String msg){
        super(msg);
    }
}