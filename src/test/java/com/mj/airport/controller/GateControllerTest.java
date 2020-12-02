/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.dto.AirplaneDto;
import com.mj.airport.dto.AvailabilityDto;
import com.mj.airport.service.FlightService;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author marko
 */
@Slf4j
public class GateControllerTest extends ControllerTest {

    @Autowired
    FlightService flightService;

    private final String addAvailabilityUri = "/gate/addAvailability?gateId=1";

    @Test
    public void addAvailabilitySuccess() {
        log.debug("perform add new flight");
        String jwtResponse = performSuccessfullLogin();
        HttpHeaders headers = headers();
        headers.add("Authorization", "Bearer " + jwtResponse);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        AvailabilityDto dto = new AvailabilityDto();
        dto.setStartTime(LocalDateTime.of(2020, Month.MARCH, 15, 8, 0, 0));
        dto.setEndTime(LocalDateTime.of(2020, Month.MARCH, 15, 9, 0, 0));
        List<AvailabilityDto> dtos = new ArrayList<>();
        dtos.add(dto);
        ResponseEntity response = restTemplate.exchange(addAvailabilityUri, HttpMethod.POST, new HttpEntity<>(dtos, headers), Object.class);
        Assert.assertEquals("Success response status", HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    public void addAvailabilityFail() {
        log.debug("perform add new flight");
        String jwtResponse = performSuccessfullLogin();
        HttpHeaders headers = headers();
        headers.add("Authorization", "Bearer " + jwtResponse);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        AvailabilityDto dto = new AvailabilityDto();
        dto.setStartTime(LocalDateTime.of(2020, Month.MARCH, 15, 18, 0, 0));
        dto.setEndTime(LocalDateTime.of(2020, Month.MARCH, 15, 9, 0, 0));
        List<AvailabilityDto> dtos = new ArrayList<>();
        dtos.add(dto);
        ResponseEntity response = restTemplate.exchange(addAvailabilityUri, HttpMethod.POST, new HttpEntity<>(dtos, headers), Object.class);
        LinkedHashMap<String, Object> resp = (LinkedHashMap<String, Object>) response.getBody();
        //Error expected, bad request, since start time is after end time
        Assert.assertEquals("Error with status code 400", HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

}
