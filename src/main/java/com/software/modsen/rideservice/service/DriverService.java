package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.software.modsen.rideservice.util.ExceptionMessages.AVAILABLE_DRIVER_NOT_FOUND;
import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverClient driverClient;
    private final RideRepository rideRepository;

    private static final int TIMEOUT_TIME = 50000;

    @Value("${rabbitmq.queues.request}")
    private String requestQueue;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;

    public ResponseEntity<DriverResponse> updateRating(DriverRatingRequest driverRatingRequest) {
        Long driverId = rideRepository.findById(driverRatingRequest.getRideId())
                .orElseThrow( () -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, driverRatingRequest.getRideId())))
                .getDriverId();
        driverRatingRequest.setDriverId(driverId);
        return driverClient.updateRating(driverRatingRequest);
    }

    public Long findAvailableDriver() {
        String temporaryQueue = UUID.randomUUID().toString();
        Queue responseQueue = new Queue(temporaryQueue, false, true, true);
        rabbitAdmin.declareQueue(responseQueue);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setReplyTo(temporaryQueue);
        Message message = new Message("".getBytes(), messageProperties);

        rabbitTemplate.convertAndSend(requestQueue, message);

        Message responseMessage = rabbitTemplate.receive(temporaryQueue, TIMEOUT_TIME);
        if (responseMessage != null) {
            rabbitAdmin.deleteQueue(temporaryQueue);
            return Long.valueOf(new String(responseMessage.getBody()));
        } else {

            rabbitAdmin.deleteQueue(temporaryQueue);
            throw new RuntimeException(AVAILABLE_DRIVER_NOT_FOUND);
        }
    }
}

