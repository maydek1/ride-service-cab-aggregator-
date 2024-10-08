package com.software.modsen.rideservice.component.steps;

import com.software.modsen.rideservice.client.DriverClient;
import com.software.modsen.rideservice.client.PassengerClient;
import com.software.modsen.rideservice.dto.request.RideStatusRequest;
import com.software.modsen.rideservice.dto.response.PassengerResponse;
import com.software.modsen.rideservice.exception.NotEnoughMoneyException;
import com.software.modsen.rideservice.exception.RideNotFoundException;
import com.software.modsen.rideservice.model.Ride;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.repositories.RideRepository;
import com.software.modsen.rideservice.service.DriverService;
import com.software.modsen.rideservice.service.RideService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static com.software.modsen.rideservice.util.ExceptionMessages.NOT_ENOUGH_MONEY;
import static com.software.modsen.rideservice.util.ExceptionMessages.RIDE_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
public class RideSteps {

    @InjectMocks
    private RideService rideService;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverClient driverClient;

    @Mock
    private DriverService driverService;

    @Mock
    private PassengerClient passengerClient;

    private Ride ride;
    private Long rideId;
    private PassengerResponse passengerResponse;
    private Exception exception;

    @Given("a ride with id {long} exists")
    public void aRideWithIdExists(Long id) {
        rideId = id;
        ride = new Ride();
        ride.setId(id);
        ride.setStatus(RideStatus.CREATED);
        rideRepository.save(ride);
    }

    @Given("a ride with id {long} and passenger id {long} and price {double}")
    public void aRideWithIdAndPrice(Long id, Long passengerId, double money) {
        rideId = id;
        ride = new Ride();
        ride.setId(id);
        ride.setPrice(BigDecimal.valueOf(money));
        ride.setPassengerId(passengerId);
        ride.setStatus(RideStatus.CREATED);
        rideRepository.save(ride);
    }

    @When("I request to get the ride by id {long}")
    public void iRequestToGetTheRideById(Long id) {
        when(rideRepository.findById(any())).thenReturn(Optional.ofNullable(ride));
        ride = rideService.getRideById(id);
    }

    @Then("I should receive a ride with id {long}")
    public void iShouldReceiveARideWithId(Long id) {
        Assertions.assertNotNull(ride);
        Assertions.assertEquals(id, ride.getId());
    }

    @When("I delete the ride by id {long}")
    public void iDeleteTheRideById(Long id) {
        when(rideRepository.findById(any())).thenReturn(Optional.ofNullable(ride));
        rideService.deleteRideById(id);
    }

    @Then("the ride with id {long} should not be found")
    public void theRideWithIdShouldNotBeFound(Long id) {
        when(rideRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(RideNotFoundException.class, () -> rideService.getRideById(id));
    }

    @When("I update the ride with id {long} to status {string}")
    public void iUpdateTheRideWithIdToStatus(Long id, String status) {
        when(rideRepository.findById(any())).thenReturn(Optional.ofNullable(ride));
        ride = rideService.getRideById(id);
        ride.setStatus(RideStatus.valueOf(status));
        rideService.updateRide(id, ride);
    }

    @When("I create a new ride with price {double} and status {string}")
    public void iCreateANewRideWithPriceAndStatus(double price, String status) {
        ride = new Ride();
        ride.setPrice(BigDecimal.valueOf(price));
        ride.setStatus(RideStatus.valueOf(status));
        rideService.createRide(ride);
    }

    @Then("the ride should have price {double} and status {string}")
    public void theRideShouldHavePriceAndStatus(double price, String status) {
        Assertions.assertEquals(BigDecimal.valueOf(price), ride.getPrice());
        Assertions.assertEquals(RideStatus.valueOf(status), ride.getStatus());
    }

    @When("I change the status of the ride with id {long} to {string}")
    public void iChangeTheStatusOfTheRide(Long id, String status) {
        when(rideRepository.findById(any())).thenReturn(Optional.ofNullable(ride));
        RideStatusRequest rideStatusRequest = new RideStatusRequest();
        rideStatusRequest.setRideId(id);
        rideStatusRequest.setStatus(RideStatus.valueOf(status));
        rideService.changeStatus(rideStatusRequest);
    }

    @Then("the ride with id {long} should have status {string}")
    public void theRideWithIdShouldHaveStatus(Long id, String status) {
        Ride updatedRide = rideService.getRideById(id);
        Assertions.assertEquals(RideStatus.valueOf(status), updatedRide.getStatus());
    }

    @When("I start a ride with price {double}")
    public void iStartARideWithPrice(double price) {
        ride = new Ride();
        ride.setPrice(BigDecimal.valueOf(price));
        ride.setPassengerId(passengerResponse.getId());
        ride.setStatus(RideStatus.CREATED);

        when(rideRepository.findById(any()))
                .thenReturn(Optional.ofNullable(ride));
        when(rideRepository.save(any()))
                .thenReturn(ride);
        when(passengerClient.getPassengerById(ride.getPassengerId()))
                .thenReturn(ResponseEntity.ok(passengerResponse));
        when(driverService.findAvailableDriver())
                .thenReturn(1L);

        try {
            ride = rideService.startRide(ride);
        }
        catch (NotEnoughMoneyException notEnoughMoneyException){
            exception = notEnoughMoneyException;
        }

    }

    @Then("a NotEnoughMoneyException should be thrown")
    public void aNotEnoughMoneyExceptionShouldBeThrown() {
        Assertions.assertEquals(exception.getMessage(), new NotEnoughMoneyException(NOT_ENOUGH_MONEY).getMessage());
    }


    @Given("a passenger with id {long} and money {double}")
    public void aPassengerWithIdAndMoney(Long id, double money) {
        passengerResponse = new PassengerResponse();
        passengerResponse.setId(id);
        passengerResponse.setMoney(BigDecimal.valueOf(money));
    }

    @Then("the ride should be started successfully")
    public void theRideShouldBeStartedSuccessfully() {
        Assertions.assertEquals(ride.getPassengerId(), passengerResponse.getId());
    }

    @Given("a ride with id {long} does not exists")
    public void aRideWithIdDoesNotExists(long id) {
        when(rideRepository.findById(id)).thenReturn(Optional.empty());
    }

    @When("I try to get a ride with id {long}")
    public void iTryToGetARideWithId(long id) {
        try {
            rideId = id;
            rideService.getRideById(id);
        }catch (RideNotFoundException ex){
            exception = ex;
        }

    }

    @Then("a RideNotFoundException should be thrown")
    public void aRideNotFoundExceptionShouldBeThrown() {
        Assertions.assertEquals(exception.getMessage(),
                new RideNotFoundException(String.format(RIDE_NOT_FOUND, rideId)).getMessage());
    }
}
