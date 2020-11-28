/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.GateDto;
import com.mj.airport.model.Gate;
import com.mj.airport.repository.GateRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ModelMapper mapper;
    
    public GateDto update(GateDto dto) {
        Gate gate = gateRepository.getOne(dto.getId());
        //update gate
        gate.setAvailable(dto.isAvailable());
        gate.setNumber(dto.getNumber());
        
        gate = gateRepository.save(gate);
        //return updatedGateDto
        return mapper.map(gate, GateDto.class);
    }
}
