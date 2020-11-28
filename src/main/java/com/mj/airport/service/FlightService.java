/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.FlightDto;
import com.mj.airport.dto.GateDto;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author marko
 */
@Slf4j
@Service
@Transactional
public class FlightService {
    public GateDto assignFlightToGate(FlightDto dto) {
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
        //return is
        return true;
    }
}
