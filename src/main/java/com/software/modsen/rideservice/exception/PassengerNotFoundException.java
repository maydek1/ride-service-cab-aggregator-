package com.software.modsen.rideservice.exception;

public class PassengerNotFoundException extends RuntimeException{
    public PassengerNotFoundException(String msg){
        super(msg);
    }
}