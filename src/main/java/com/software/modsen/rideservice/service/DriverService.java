package com.software.modsen.rideservice.service;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.dto.request.DriverRatingRequest;
import com.software.modsen.rideservice.dto.request.DriverResponse;
import com.software.modsen.rideservice.exception.DriverNotFoundException;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static com.software.modsen.rideservice.util.ExceptionMessages.AVAILABLE_DRIVER_NOT_FOUND;
import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverClient driverClient;
    private final RideRepository rideRepository;


    private static final int TIMEOUT_TIME = 10000;
    private static final int MAX_RETRIES = 5;

    private final RabbitTemplate rabbitTemplate;
    private final Queue responseQueue;
    private final Queue requestQueue;
    public ResponseEntity<DriverResponse> updateRating(DriverRatingRequest driverRatingRequest) {
        Long driverId = rideRepository.findById(driverRatingRequest.getRideId())
                .orElseThrow( () -> new RideNotFoundException(String.format(RIDE_NOT_FOUND, driverRatingRequest.getRideId())))
                .getDriverId();
        driverRatingRequest.setDriverId(driverId);
        return driverClient.updateRating(driverRatingRequest);
    }

    private void attemptToFindDriver(CompletableFuture<Long> futureResponse, int remainingRetries, long timeout, Long correlationId) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(correlationId.toString());
        messageProperties.setReplyTo(responseQueue.getName());
        Message message = new Message("".getBytes(), messageProperties);

        rabbitTemplate.convertAndSend(requestQueue.getName(), message);

        Message responseMessage = rabbitTemplate.receive(responseQueue.getName(), timeout);

        if (responseMessage != null) {
            futureResponse.complete(Long.valueOf(new String(responseMessage.getBody())));
        } else {
            if (remainingRetries > 0) {
                CompletableFuture.delayedExecutor(timeout, TimeUnit.MILLISECONDS).execute(() ->
                        attemptToFindDriver(futureResponse, remainingRetries - 1, timeout, correlationId)
                );
            } else {
                futureResponse.completeExceptionally(new RuntimeException(AVAILABLE_DRIVER_NOT_FOUND));
            }
        }
    }

    public long findAvailableDriver() {
        Long correlationId = UUID.randomUUID().getMostSignificantBits();

        CompletableFuture<Long> futureResponse = new CompletableFuture<>();

        attemptToFindDriver(futureResponse, MAX_RETRIES, TIMEOUT_TIME, correlationId);

        try {
            return futureResponse.join();
        } catch (CompletionException ex) {
            throw new DriverNotFoundException(AVAILABLE_DRIVER_NOT_FOUND);
        }
    }
}

