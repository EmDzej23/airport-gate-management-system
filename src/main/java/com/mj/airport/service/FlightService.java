/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.AirplaneDto;
import com.mj.airport.dto.FlightDto;
import com.mj.airport.model.Airplane;
import com.mj.airport.model.Flight;
import com.mj.airport.model.Gate;
import com.mj.airport.repository.AirplaneRepository;
import com.mj.airport.repository.FlightRepository;
import com.mj.airport.repository.GateRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author marko
 */
@Slf4j
@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirplaneRepository airplaneRepository;
    @Autowired
    private GateRepository gateRepository;
    @Autowired
    private ModelMapper mapper;

    @PostConstruct
    public void createInitFlight() {
        if (flightRepository.findByNumber("number_1").isPresent() && flightRepository.findByNumber("number_11").isPresent()) {
            return;
        }
        log.info("creating initial flight number_1");
        AirplaneDto airplane1 = new AirplaneDto();
        airplane1.setModel("model1");

        create(airplane1, "number_1");

        log.info("creating initial flight number_11");
        AirplaneDto airplane2 = new AirplaneDto();
        airplane2.setModel("model2");

        create(airplane2, "number_11");
        
        log.info("creating initial flight number_111");
        AirplaneDto airplane3 = new AirplaneDto();
        airplane3.setModel("model3");

        create(airplane3, "number_111");
    }

    public Flight getFlightByNumber(String number) {
        return flightRepository.findByNumber(number).get();
    }

    @Retryable(value = {ObjectOptimisticLockingFailureException.class, StaleObjectStateException.class})
    @Transactional(rollbackOn = {StaleObjectStateException.class}, value = Transactional.TxType.REQUIRES_NEW)
    @Async
    public CompletableFuture<ResponseEntity> assignFlightToGate(String number) {

        Flight flight = getFlightByNumber(number);
        return CompletableFuture.completedFuture(finishGateAssigning(flight));

    }

    public ResponseEntity finishGateAssigning(Flight flight) {
        if (flight.getGate() != null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Gate is already assigned to this flight.");
        }
        Gate availableGate = findAvailableGate();
        //if no gate is available return error message
        if (availableGate == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Currently, there is no available gate. Please try later.");
        } //if available, assign gate to flight
        else {
            flight = updateFlight(flight, availableGate);
            return ResponseEntity.ok(mapper.map(flight, FlightDto.class));
        }
    }
    
    public Gate findAvailableGate() {
        Pageable pageable = PageRequest.of(0, 1);
        Gate gate = gateRepository.findAvailableGate(LocalDateTime.now(), pageable).get().findAny().orElse(null);
        return gate;
    }

    public Flight updateFlight(Flight flight, Gate gate) {
        flight.setGate(gate);
        flight = flightRepository.saveAndFlush(flight);
        gate.setAvailable(false);
        gateRepository.saveAndFlush(gate);
        return flight;
    }

    @Transactional
    public ResponseEntity create(AirplaneDto airplaneDto, String number) {
        Flight flight = new Flight();
        flight.setAirplane(createAirplane(airplaneDto));
        flight.setNumber(number);
        flight.setGate(null);
        flightRepository.saveAndFlush(flight);

        return ResponseEntity.ok(mapper.map(flight, FlightDto.class
        ));
    }

    @Transactional
    public Airplane
            createAirplane(AirplaneDto airplaneDto) {
        Airplane airplane = mapper.map(airplaneDto, Airplane.class);
        airplane = airplaneRepository.saveAndFlush(airplane);
        return airplane;
    }

    
}
