/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.config;

import com.mj.airport.dto.FlightDto;
import com.mj.airport.model.Flight;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author marko
 */
@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        mapper.createTypeMap(Flight.class, FlightDto.class).addMappings(m -> {
            m.map(flight -> flight.getAirplane().getId(), FlightDto::setAirplane);
            m.map(flight -> flight.getGate().getId(), FlightDto::setGate);
        });
        
        return mapper;
    }
}

