/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.AirplaneDto;
import com.mj.airport.dto.FlightDto;
import com.mj.airport.dto.GateDto;
import com.mj.airport.model.Airplane;
import com.mj.airport.model.Flight;
import com.mj.airport.model.Gate;
import com.mj.airport.repository.AirplaneRepository;
import com.mj.airport.repository.FlightRepository;
import com.mj.airport.repository.GateRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author marko
 */
@Slf4j
@Service
@Transactional
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
        if (flightRepository.findByNumber("number_1").isPresent()) {
            return;
        }
        log.info("creating initial flight number_1");
        AirplaneDto airplane = new AirplaneDto();
        airplane.setModel("model1");

        create(airplane, "number_1");
    }

    public ResponseEntity assignFlightToGate(String number) {
        //find first available gate
        Gate availableGate = findAvailableGate();
        //if no gate is available return error message
        if (availableGate == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(Arrays.asList("There is no available gate"));
        } //if available, assign gate to flight
        else {
            Flight flight = flightRepository.findByNumber(number).get();
            flight.setGate(availableGate);
            flight = flightRepository.saveAndFlush(flight);
            availableGate.setAvailable(false);
            gateRepository.saveAndFlush(availableGate);
            return ResponseEntity.ok(mapper.map(flight, FlightDto.class));
        }
    }

    public ResponseEntity create(AirplaneDto airplaneDto, String number) {
        Flight flight = new Flight();
        flight.setAirplane(createAirplane(airplaneDto));
        flight.setNumber(number);
        flight.setGate(null);
        flightRepository.saveAndFlush(flight);
        return ResponseEntity.ok(mapper.map(flight, FlightDto.class));
    }

    public Airplane createAirplane(AirplaneDto airplaneDto) {
        Airplane airplane = mapper.map(airplaneDto, Airplane.class);
        airplane = airplaneRepository.saveAndFlush(airplane);
        return airplane;
    }
    
    public Gate findAvailableGate() {
        Pageable pageable = PageRequest.of(0, 1);
        Gate gate = gateRepository.findAvailableGate(LocalDateTime.now(),pageable).get().findFirst().get();
        return gate;
    }
}
