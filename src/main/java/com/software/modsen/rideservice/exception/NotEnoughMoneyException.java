package com.software.modsen.rideservice.exception;

public class NotEnoughMoneyException extends RuntimeException{
    public NotEnoughMoneyException(String msg){
        super(msg);
    }
}
