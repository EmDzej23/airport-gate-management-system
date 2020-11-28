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
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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
public class GateService {
    @Autowired
    private GateRepository gateRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private ModelMapper mapper;
    
    //Create init gates
    @PostConstruct
    private void createInitialUser() {
        if (!gateRepository.findByNumber("1").isPresent()) {
            log.info("creating initial gate 1");
            Gate gate = new Gate();
            gate.setAvailable(true);
            gate.setNumber("1");
            create(mapper.map(gate, GateDto.class));
        }
        if (!gateRepository.findByNumber("2").isPresent()) {
            log.info("creating initial gate 2");
            Gate gate = new Gate();
            gate.setAvailable(true);
            gate.setNumber("2");
            create(mapper.map(gate, GateDto.class));
        }
        if (!gateRepository.findByNumber("3").isPresent()) {
            log.info("creating initial gate 3");
            Gate gate = new Gate();
            gate.setAvailable(false);
            gate.setNumber("3");
            create(mapper.map(gate, GateDto.class));
        }
        
    }
    
    public ResponseEntity update(GateDto dto) {
        Gate gate = gateRepository.getOne(dto.getId());
        //update gate
        gate.setAvailable(dto.isAvailable());
        gate.setNumber(dto.getNumber());
        
        gate = gateRepository.save(gate);
        //return updatedGateDto
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }
    
    public ResponseEntity setGateAvailable(Long gateId, boolean available) {
        Gate gate = gateRepository.getOne(gateId);
        //update gate
        gate.setAvailable(available);
        
        gate = gateRepository.save(gate);
        //return updatedGateDto
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }
    
    public ResponseEntity create(GateDto gateDto) {
        Gate gate = mapper.map(gateDto, Gate.class);
        gate = gateRepository.saveAndFlush(gate);
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }
    
    public ResponseEntity listAll() {
        List<Gate> gates = gateRepository.findAll();
        return ResponseEntity.ok(gates.stream().map(gate -> mapper.map(gate, GateDto.class)).collect(Collectors.toList()));
    }
    
    public ResponseEntity listAllAvailabilities(Long gateId) {
        List<Availability> availabilities = availabilityRepository.findByGateId(gateId);
        return ResponseEntity.ok(availabilities.stream().map(availability -> mapper.map(availability, AvailabilityDto.class)).collect(Collectors.toList()));
    }
    
    public ResponseEntity setAvailabilitiesForGate(List<AvailabilityDto> availabilities, Long gateId) {
        Gate gate = gateRepository.findById(gateId).get();
        availabilities.stream().forEach(availability -> {
            Availability availabilityToAdd = mapper.map(availability, Availability.class);
            availabilityToAdd.setGate(gate);
            availabilityRepository.save(availabilityToAdd);
        });
        
        return ResponseEntity.ok(mapper.map(gate, GateDto.class));
    }
    
    public ResponseEntity updateAvailability(AvailabilityDto availability, Long availabilityId) {
        Availability availabilityToUpdate = availabilityRepository.findById(availabilityId).get();
        availabilityToUpdate.setEndTime(availability.getEndTime());
        availabilityToUpdate.setStartTime(availability.getStartTime());
        availabilityRepository.save(availabilityToUpdate);
        
        return ResponseEntity.ok(mapper.map(availabilityToUpdate, AvailabilityDto.class));
    }
    
    public ResponseEntity deleteAvailabilities(Long gateId) {
        availabilityRepository.deleteByGateId(gateId);
        return ResponseEntity.ok("All availabilities deleted for gate: "+gateId);
    }
    
    
    
}
