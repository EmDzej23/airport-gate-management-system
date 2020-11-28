/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.auth.Constants;
import com.mj.airport.dto.AirplaneDto;
import com.mj.airport.dto.FlightDto;
import com.mj.airport.service.FlightService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author marko
 */
@Api
@Slf4j
@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    FlightService flightService;

    @PostMapping("/")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send POST request to create new flight based on airplane data", httpMethod = "POST", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity create(@ApiParam(value = "AirplaneDto model", required = true) @RequestBody @Valid AirplaneDto airplaneDto, @ApiParam(value = "Flight number", required = true) @RequestParam @Valid String number) {
        return flightService.create(airplaneDto, number);
    }

    @PostMapping("/assign-gate")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send POST request to assign particuler gate to the flight", httpMethod = "POST", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity assignGate(@ApiParam(value = "Flight number", required = true) @RequestParam @Valid String number) {
        return flightService.assignFlightToGate(number);
    }

}
