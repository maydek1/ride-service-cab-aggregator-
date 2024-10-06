package com.software.modsen.rideservice.exception;

public class ServiceUnavailableException extends RuntimeException{
    public ServiceUnavailableException(String msg){
        super(msg);
    }
}
