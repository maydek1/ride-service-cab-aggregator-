package com.software.modsen.rideservice.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    private static final int RETRYER_PERIOD = 1000;
    public static final int RETRYER_MAX_PERIOD = 5000;
    public static final int RETRYER_MAX_ATTEMPTS = 3;
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(RETRYER_PERIOD, RETRYER_MAX_PERIOD, RETRYER_MAX_ATTEMPTS);
    }
}
