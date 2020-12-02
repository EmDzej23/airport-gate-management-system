/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.dto.AirplaneDto;
import com.mj.airport.service.FlightService;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
public class FlightControllerTest extends ControllerTest {

    @Autowired
    FlightService flightService;

    private final String[] flightNumbers = {"number_1", "number_11"};
    private final String addNewFlightUri = "/flight/?number=number1";

    @Test
    public void assignFlightToGate() throws InterruptedException {
        log.debug("perform flight assign to gate");
        final ExecutorService executor = Executors.newFixedThreadPool(flightNumbers.length);

        //given
        
        //3 flights, and only 2 available gates
        
        //when
        
        //we assign 2 of 3
        for (final String flightNumber : flightNumbers) {
            executor.execute(() -> {
                try {
                    ResponseEntity r = flightService.assignFlightToGate(flightNumber).get();

                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(FlightControllerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        //then
        
        //we expect info messages:
        ResponseEntity r1;
        try {
            r1 = flightService.assignFlightToGate("number_111").get();
            Assert.assertEquals("Try to assign a gate when none is available", "Currently, there is no available gate. Please try later.", r1.getBody().toString());
            r1 = flightService.assignFlightToGate("number_1").get();
            Assert.assertEquals("Try to assign a gate to already assigned flight", "Gate is already assigned to this flight.", r1.getBody().toString());
        } catch (ExecutionException ex) {
            Logger.getLogger(FlightControllerTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Test
    public void addNewFlight() {
        log.debug("perform add new flight");
        String jwtResponse = performSuccessfullLogin();
        HttpHeaders headers = headers();
        headers.add("Authorization", "Bearer " + jwtResponse);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        AirplaneDto dto = new AirplaneDto();
        dto.setModel("model123");
        ResponseEntity response = restTemplate.exchange(addNewFlightUri, HttpMethod.POST, new HttpEntity<>(dto, headers), Object.class);
        LinkedHashMap<String, Object> resp = (LinkedHashMap<String, Object>) response.getBody();
        Assert.assertEquals("Id of entered flight is 4", 4, resp.get("id"));
    }
    
    @Test
    public void addNewFlightDuplicateName() {
        log.debug("perform add new flight");
        String jwtResponse = performSuccessfullLogin();
        HttpHeaders headers = headers();
        headers.add("Authorization", "Bearer " + jwtResponse);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        AirplaneDto dto = new AirplaneDto();
        dto.setModel("model1");
        ResponseEntity response = restTemplate.exchange(addNewFlightUri, HttpMethod.POST, new HttpEntity<>(dto, headers), Object.class);
        LinkedHashMap<String, Object> resp = (LinkedHashMap<String, Object>) response.getBody();
        
        //Unique index or primary key violation error expected
        Assert.assertEquals("Error with status code 500", HttpStatus.INTERNAL_SERVER_ERROR.value(), Integer.parseInt(resp.get("errorCode").toString()));
    }
    
    
}
