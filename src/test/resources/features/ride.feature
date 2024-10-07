Feature: Testing Ride Service

  Scenario: Get a ride by ID
    Given a ride with id 1 exists
    When I request to get the ride by id 1
    Then I should receive a ride with id 1

  Scenario: Delete a ride by ID
    Given a ride with id 1 exists
    When I delete the ride by id 1
    Then the ride with id 1 should not be found

  Scenario: Update a ride status
    Given a ride with id 1 exists
    When I update the ride with id 1 to status "ACCEPTED"
    Then the ride with id 1 should have status "ACCEPTED"

  Scenario: Create a new ride
    When I create a new ride with price 100.0 and status "CREATED"
    Then the ride should have price 100.0 and status "CREATED"

  Scenario: Change ride status
    Given a ride with id 1 exists
    When I change the status of the ride with id 1 to "COMPLETED"
    Then the ride with id 1 should have status "COMPLETED"

  Scenario: Start a ride with enough money
    Given a passenger with id 1 and money 50.0
    When I start a ride with price 50.0
    Then the ride should be started successfully

  Scenario: Start a ride with not enough money
    Given a passenger with id 1 and money 50.0
    When I start a ride with price 100.0
    Then a NotEnoughMoneyException should be thrown

  Scenario: Trying to get a non-existent ride
    Given a ride with id 999 does not exists
    When I try to get a ride with id 999
    Then a RideNotFoundException should be thrown
