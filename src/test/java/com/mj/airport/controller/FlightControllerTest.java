/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.service.FlightService;
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

    @Test
    public void getAllSuccessTest() throws InterruptedException {
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
        //we expect info message:
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
}
