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
import com.sun.xml.internal.ws.util.CompletedFuture;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
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

    /*
    In service layer I return ResponseEntity on every method.
    I believe this is not the best solution.
    I wanted to avoid ANY logic inside controllers, so that is the main reason.
    In real system, I would probably create abstract service or interface(s) along
    with the CustomDto containing e.g. concrete dto, response type and message.
     */
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private AirplaneRepository airplaneRepository;
    @Autowired
    private GateRepository gateRepository;
    @Autowired
    private ModelMapper mapper;

    //Started data init
    @PostConstruct
    public void createInitFlights() {
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

    //We fetch a flight by number since this field is unique
    public Flight getFlightByNumber(String number) {
        return flightRepository.findByNumber(number).get();
    }

    //Retry this call when some of the 2 exc is thrown
    //It will happen when 2 or more requests try to find and assign available gate to the flight at the same time
    //Possible improvement: try multiple times | introduce pessimistic locking
    //This method is async since we want to enable users access it parallel, this service is most important in this demo
    @Retryable(value = {ObjectOptimisticLockingFailureException.class, StaleObjectStateException.class})
    @Transactional(rollbackOn = {StaleObjectStateException.class}, value = Transactional.TxType.REQUIRES_NEW)
    @Async("taskExecutor")
    public CompletableFuture<ResponseEntity> assignFlightToGate(String number) {

        Flight flight = getFlightByNumber(number);
        return CompletableFuture.completedFuture(finishGateAssigning(flight));

    }

    //No transactional here and to the bottom, since we want all of 'down' calls to be inside 'top' trans: assignFlightToGate 
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

    //Search for the available gate based on 'available' boolean field and availability table rows
    //Get only 1 - first gate that is available (here we want to get the locking exception, and not allow different users to assign same gate)
    public Gate findAvailableGate() {
        Pageable pageable = PageRequest.of(0, 1);
        //Using simply current time of the server request landed on
        //Possible improvement: Add flight date so it could be set by admin
        Gate gate = gateRepository.findAvailableGate(LocalDateTime.now(), pageable).get().findAny().orElse(null);
        return gate;
    }

    //Flight is being assigned to the gate
    //Gate is now unavailable
    //Possible improvements: If someone wanted this gate to be unavailable 'a second' before 
    //this action, we should check that and unasign flight from this gate eventually
    public Flight updateFlight(Flight flight, Gate gate) {
        flight.setGate(gate);
        flight = flightRepository.saveAndFlush(flight);
        gate.setAvailable(false);
        gateRepository.saveAndFlush(gate);
        return flight;
    }

    //Insert new flight in database, along with the plane
    //One plane can have multiple flights so it is better to divide them
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ResponseEntity> create(AirplaneDto airplaneDto, String number) {
        Flight flight = new Flight();
        flight.setAirplane(createAirplane(airplaneDto));
        flight.setNumber(number);
        flight.setGate(null);
        flightRepository.saveAndFlush(flight);
        return CompletableFuture.completedFuture(ResponseEntity.ok(mapper.map(flight, FlightDto.class)));
    }

    //Airplane can be created seperately
    @Transactional
    public Airplane
            createAirplane(AirplaneDto airplaneDto) {
        Airplane airplane = mapper.map(airplaneDto, Airplane.class);
        airplane = airplaneRepository.saveAndFlush(airplane);
        return airplane;
    }

}
