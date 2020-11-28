/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.auth.Constants;
import com.mj.airport.dto.AirplaneDto;
import com.mj.airport.dto.FlightDto;
import com.mj.airport.dto.GateDto;
import com.mj.airport.service.FlightService;
import com.mj.airport.service.GateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author marko
 */
@Api
@Slf4j
@RestController
@RequestMapping("/gate")
public class GateController {

    @Autowired
    GateService gateService;

    @PutMapping("{id}/available/{available}")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send PUT request to set gate available", httpMethod = "PUT", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity update(@ApiParam(value = "Gate ID", required = true) @PathVariable Long id, @ApiParam(value = "Gate availability", required = true) @PathVariable boolean available) {
        return gateService.setGateAvailable(id, available);
    }
    
    @PostMapping("/")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send POST request to create new gate", httpMethod = "POST", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity create(@ApiParam(value = "GateDto model", required = true) @RequestBody @Valid GateDto gateDto) {
        return gateService.create(gateDto);
    }
    
    @GetMapping("/")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send GET request to fetch all gates", httpMethod = "GET", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity findAll() {
        return gateService.listAll();
    }
    
    

}
