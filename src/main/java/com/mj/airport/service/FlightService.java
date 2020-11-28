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
import com.mj.airport.repository.AirplaneRepository;
import com.mj.airport.repository.FlightRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ModelMapper mapper;
    public ResponseEntity assignFlightToGate(FlightDto dto) {
        //find first available gate
        boolean isGateAvailable = isGateAvailable(dto);
        //if no gate is available return error message
        //if available, assign gate to flight
        //set gate unavailable
        //save flight
        //return gateDto
        return null;
    }
    
    public boolean isGateAvailable(FlightDto dto) {
        return true;
    }
    
    public ResponseEntity create(AirplaneDto airplaneDto) {
        Airplane airplane = mapper.map(airplaneDto, Airplane.class);
        System.out.println("airplane" +airplane);
        airplane = airplaneRepository.saveAndFlush(airplane);
        Flight flight = new Flight();
        flight.setAirplane(airplane);
        flight.setGate(null);
        flightRepository.saveAndFlush(flight);
        return ResponseEntity.ok(mapper.map(flight, FlightDto.class));
    }
}
