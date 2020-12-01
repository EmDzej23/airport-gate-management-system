/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.AvailabilityDto;
import com.mj.airport.dto.GateDto;
import com.mj.airport.model.Availability;
import com.mj.airport.model.Gate;
import com.mj.airport.repository.AvailabilityRepository;
import com.mj.airport.repository.GateRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
public class GateService {
    
    /*
    In service layer I return ResponseEntity on every method.
    I believe this is not the best solution.
    I wanted to avoid ANY logic inside controllers, so that is the main reason.
    In real system, I would probably create abstract service or interface(s) along
    with the CustomDto containing e.g. concrete dto, response type and message.
    */

    @Autowired
    private GateRepository gateRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private ModelMapper mapper;

    //Started data init
    @PostConstruct
    private void createInitialGatesAndAvailabilities() {
        if (!gateRepository.findByNumber("1").isPresent()) {
            log.info("creating initial gate 1");
            Gate gate = new Gate();
            gate.setAvailable(false);
            gate.setNumber("1");
            gate = createInitGate(mapper.map(gate, GateDto.class));
            Availability availability1 = new Availability();
            Availability availability2 = new Availability();
            Availability availability3 = new Availability();
            availability1.setStartTime(LocalDateTime.of(2020, 11, 29, 9, 0));
            availability1.setEndTime(LocalDateTime.of(2020, 11, 29, 10, 0));
            availability1.setGate(gate);
            availabilityRepository.saveAndFlush(availability1);
            availability2.setStartTime(LocalDateTime.of(2020, 11, 29, 15, 0));
            availability2.setEndTime(LocalDateTime.of(2020, 11, 29, 16, 0));
            availability2.setGate(gate);
            availabilityRepository.saveAndFlush(availability2);
            availability3.setStartTime(LocalDateTime.of(2020, 11, 29, 17, 35));
            availability3.setEndTime(LocalDateTime.of(2022, 11, 29, 20, 0));
            availability3.setGate(gate);
            availabilityRepository.saveAndFlush(availability3);

        }
        if (!gateRepository.findByNumber("2").isPresent()) {
            log.info("creating initial gate 2");
            Gate gate = new Gate();
            gate.setAvailable(true);
            gate.setNumber("2");
            createInitGate(mapper.map(gate, GateDto.class));
        }
        if (!gateRepository.findByNumber("3").isPresent()) {
            log.info("creating initial gate 3");
            Gate gate = new Gate();
            gate.setAvailable(true);
            gate.setNumber("3");
            createInitGate(mapper.map(gate, GateDto.class));
        }

    }

    //starter data
    public Gate createInitGate(GateDto gateDto) {
        Gate gate = mapper.map(gateDto, Gate.class);
        gate = gateRepository.saveAndFlush(gate);
        return gate;
    }

    //update gate
    public ResponseEntity update(GateDto dto) {
        Gate gate = gateRepository.getOne(dto.getId());
        //update gate
        gate.setAvailable(dto.isAvailable());
        gate.setNumber(dto.getNumber());

        gate = gateRepository.save(gate);
        
        //return updatedGateDto
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }

    //Update gate's availability
    public ResponseEntity setGateAvailable(Long gateId, boolean available) {
        Gate gate = gateRepository.getOne(gateId);

        //It may be admin's mistake, but it may be that gate has just bean assingned to a gate, at similar time admin tried to set it as unavailable
        if (gate.isAvailable() == available && !available) {
            //Possible improvements: 
            // - check if gate has assigned flight
            // - unasign gate if we need it unavailable without flights
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Gate status is the same. It may have just been updated to " + available + ". //Possible improvement scenarios in Readme");
        }
        //update gate availability
        gate.setAvailable(available);
        gate = gateRepository.save(gate);
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }

    //Create gate
    public ResponseEntity create(GateDto gateDto) {
        Gate gate = mapper.map(gateDto, Gate.class);
        gate = gateRepository.saveAndFlush(gate);
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }

    //List all gates from the database
    //In a real system it should be pageable with skip and page fields
    public ResponseEntity listAll() {
        List<Gate> gates = gateRepository.findAll();
        return ResponseEntity.ok(gates.stream().map(gate -> mapper.map(gate, GateDto.class)).collect(Collectors.toList()));
    }

    //List all availabilities for specific gate
    //Gate availability is the time gate is available at (start and end time)
    //Here we have a list of start times and end times for one gate
    public ResponseEntity listAllAvailabilities(Long gateId) {
        List<Availability> availabilities = availabilityRepository.findByGateId(gateId);
        return ResponseEntity.ok(availabilities.stream().map(availability -> mapper.map(availability, AvailabilityDto.class)).collect(Collectors.toList()));
    }

    //Here we enter a list of availabilities for a gate
    //Some possible checks in a real system:
    ////check if entered start and end times are between existing ones
    ////check if start time is after end time
    ////check if start time is after today
    ////check if there are flights reservations prepared in time we are setting
    public ResponseEntity insertAvailabilitiesForGate(List<AvailabilityDto> availabilities, Long gateId) {
        Gate gate = gateRepository.findById(gateId).get();
        availabilities.stream().forEach(availability -> {
            Availability availabilityToAdd = mapper.map(availability, Availability.class);
            availabilityToAdd.setGate(gate);
            availabilityRepository.save(availabilityToAdd);
        });

        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }

    //update availability based on its id
    public ResponseEntity updateAvailability(AvailabilityDto availability, Long availabilityId) {
        Availability availabilityToUpdate = availabilityRepository.findById(availabilityId).get();
        availabilityToUpdate.setEndTime(availability.getEndTime());
        availabilityToUpdate.setStartTime(availability.getStartTime());
        availabilityRepository.save(availabilityToUpdate);

        return ResponseEntity.ok(mapper.map(availabilityToUpdate, AvailabilityDto.class));
    }

    //clear all availabilities for a gate
    public ResponseEntity deleteAvailabilities(Long gateId) {
        availabilityRepository.deleteByGateId(gateId);
        return ResponseEntity.ok("All availabilities deleted for gate: " + gateId);
    }

    //show available gate (same method as in flight service, here used only for swagger)
    public ResponseEntity findAvailableGate() {
        Pageable pageable = PageRequest.of(0, 1);
        Gate gate = gateRepository.findAvailableGate(LocalDateTime.now(), pageable).get().findFirst().orElse(null);
        return ResponseEntity.ok(gate == null ? "No gate found" : mapper.map(gate, GateDto.class));
    }

}
