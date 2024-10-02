package com.software.modsen.rideservice.controller;

import com.software.modsen.rideservice.dto.request.*;
import com.software.modsen.rideservice.dto.response.RideResponse;
import com.software.modsen.rideservice.dto.response.RideResponseSet;
import com.software.modsen.rideservice.mapper.RideMapper;
import com.software.modsen.rideservice.model.RideStatus;
import com.software.modsen.rideservice.service.DriverService;
import com.software.modsen.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ride")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final DriverService driverService;
    private final RideMapper rideMapper;

    @Operation(summary = "Создать поездку")
    @ApiResponse(responseCode = "201", description = "Поездка успешно создана",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @PostMapping
    public ResponseEntity<RideResponse> createRide(@RequestBody RideRequest rideRequest) {
        RideResponse createdRide = rideMapper.rideToRideResponse(rideService.createRide(
                rideMapper.rideRequestToRide(rideRequest)));
        return new ResponseEntity<>(createdRide, HttpStatus.CREATED);
    }

    @Operation(summary = "Получить поездку по ID")
    @ApiResponse(responseCode = "200", description = "Поездка найдена",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long id) {
        RideResponse ride = rideMapper.rideToRideResponse(rideService.getRideById(id));
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @Operation(summary = "Обновить поездку")
    @ApiResponse(responseCode = "200", description = "Поездка успешно обновлена",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(@PathVariable Long id, @RequestBody RideRequest rideRequest) {
        RideResponse updatedRide = rideMapper.rideToRideResponse(rideService.updateRide(id,
                rideMapper.rideRequestToRide(rideRequest)));
        return new ResponseEntity<>(updatedRide, HttpStatus.OK);
    }

    @Operation(summary = "Удалить поездку по ID")
    @ApiResponse(responseCode = "200", description = "Поездка успешно удалена",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<RideResponse> deleteRideById(@PathVariable Long id) {
        RideResponse deletedRide = rideMapper.rideToRideResponse(rideService.deleteRideById(id));
        return new ResponseEntity<>(deletedRide, HttpStatus.OK);
    }

    @Operation(summary = "Получить все поездки")
    @ApiResponse(responseCode = "200", description = "Поездки успешно получены",
            content = @Content(schema = @Schema(implementation = RideResponseSet.class)))
    @GetMapping
    public ResponseEntity<RideResponseSet> getAllRides() {
        RideResponseSet rides = new RideResponseSet(rideService.getAllRides().stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @Operation(summary = "Получить поездки по водителю")
    @ApiResponse(responseCode = "200", description = "Поездки успешно получены",
            content = @Content(schema = @Schema(implementation = RideResponseSet.class)))
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<RideResponseSet> getRidesByDriver(@PathVariable Long driverId) {
        RideResponseSet rides = new RideResponseSet(rideService.getRidesByDriver(driverId)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @Operation(summary = "Получить поездки по пассажиру")
    @ApiResponse(responseCode = "200", description = "Поездки успешно получены",
            content = @Content(schema = @Schema(implementation = RideResponseSet.class)))
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<RideResponseSet> getRidesByPassenger(@PathVariable Long passengerId) {
        RideResponseSet rides = new RideResponseSet(rideService.getRidesByPassenger(passengerId)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @Operation(summary = "Получить поездки по статусу")
    @ApiResponse(responseCode = "200", description = "Поездки успешно получены",
            content = @Content(schema = @Schema(implementation = RideResponseSet.class)))
    @GetMapping("/status/{status}")
    public ResponseEntity<RideResponseSet> getRidesByStatus(@PathVariable RideStatus status) {
        RideResponseSet rides = new RideResponseSet(rideService.getRidesByStatus(status)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet()));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @Operation(summary = "Изменить статус поездки")
    @ApiResponse(responseCode = "200", description = "Статус поездки успешно изменён",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @PutMapping("/status")
    public ResponseEntity<RideResponse> changeRideStatus(@RequestBody RideStatusRequest rideStatus) {
        RideResponse ride = rideMapper.rideToRideResponse(rideService.changeStatus(rideStatus));
        return new ResponseEntity<>(ride, HttpStatus.OK);
    }

    @Operation(summary = "Назначить водителя")
    @ApiResponse(responseCode = "200", description = "Водитель успешно назначен",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @PutMapping("/driver")
    public ResponseEntity<RideResponse> setDriver(@RequestBody RideDriverRequest request) {
        RideResponse rideResponse = rideMapper.rideToRideResponse(rideService.setDriver(request));
        return ResponseEntity.ok(rideResponse);
    }

    @Operation(summary = "Получить поездки для подтверждения")
    @ApiResponse(responseCode = "200", description = "Поездки успешно получены",
            content = @Content(schema = @Schema(implementation = RideResponseSet.class)))
    @GetMapping("/created/{id}")
    public ResponseEntity<RideResponseSet> getRideToConfirm(@PathVariable Long id) {
        return ResponseEntity.ok(new RideResponseSet(rideService.getRidesToConfirm(id)
                .stream()
                .map(rideMapper::rideToRideResponse)
                .collect(Collectors.toSet())));
    }

    @Operation(summary = "Обновить статус поездки")
    @ApiResponse(responseCode = "200", description = "Статус поездки успешно обновлён",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @PostMapping("/update-status")
    public ResponseEntity<RideResponse> updateRideStatus(
            @RequestBody RideStatusRequest statusRequest) {

        RideResponse rideResponse = rideMapper.rideToRideResponse(rideService.updateRideStatus(statusRequest));
        return ResponseEntity.ok(rideResponse);
    }

    @Operation(summary = "Обновить рейтинг водителя")
    @ApiResponse(responseCode = "200", description = "Рейтинг водителя успешно обновлён",
            content = @Content(schema = @Schema(implementation = DriverResponse.class)))
    @PostMapping("/driver")
    ResponseEntity<DriverResponse> updateRating(@RequestBody DriverRatingRequest driverRatingRequest) {
        return driverService.updateRating(driverRatingRequest);
    }

    @Operation(summary = "Начать поездку")
    @ApiResponse(responseCode = "200", description = "Поездка успешно начата",
            content = @Content(schema = @Schema(implementation = RideResponse.class)))
    @PostMapping("/ride")
    public ResponseEntity<RideResponse> startRide(@RequestBody RideRequest rideRequest) {
        return ResponseEntity.ok().body(rideMapper.rideToRideResponse(rideService.startRide(
                rideMapper.rideRequestToRide(rideRequest))));
    }
}
