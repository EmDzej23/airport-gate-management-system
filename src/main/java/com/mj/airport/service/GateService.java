/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.service;

import com.mj.airport.dto.AvailabilityDto;
import com.mj.airport.dto.FlightDto;
import com.mj.airport.dto.GateDto;
import java.util.List;
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
public class GateService {
    public GateDto update(GateDto gate) {
        //update gate
        //return updatedGate
        return null;
    }
}
