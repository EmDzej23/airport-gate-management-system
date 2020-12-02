/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.auth.Constants;
import com.mj.airport.dto.AvailabilityDto;
import com.mj.airport.dto.GateDto;
import com.mj.airport.service.GateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/gate")
public class GateController {

    @Autowired
    GateService gateService;

    @PutMapping("{id}/available/{available}")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send PUT request to set gate available", httpMethod = "PUT", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity update(@ApiParam(value = "Gate ID", required = true) @PathVariable Long id, @ApiParam(value = "Gate availability", required = true) @PathVariable boolean available) {
        //Basic handle of possible concurent issue with optimistic locking used
        //Could use Retryable on service method, or introduce pessimistic locking and additional handling
        try {
            return gateService.setGateAvailable(id, available);
        } catch (Exception e) {
            if (e instanceof ObjectOptimisticLockingFailureException
                    || e instanceof StaleObjectStateException) {
                return gateService.setGateAvailable(id, available);
            }
        }
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
    public ResponseEntity findAll() throws Throwable {
        try {
            return gateService.listAll().get();
        } catch (InterruptedException | ExecutionException ex) {
            throw ex.getCause();
        }
    }
    
    @GetMapping("/{id}/availabilities")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send GET request to fetch all availabilities for gate", httpMethod = "GET", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity findAvailabilitiesForGate(@ApiParam(value = "Gate number", required = true) @RequestParam @Valid Long gateId) throws Throwable {
        try {
            return gateService.listAllAvailabilities(gateId).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw ex.getCause();
        }
    }
    
    @PostMapping("/addAvailability")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send POST request to add time availabilities to gate", httpMethod = "POST", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity insertAvailabilities(@ApiParam(value = "AvailabilityDto model", required = true) @RequestBody @Valid List<AvailabilityDto> availabilityDtos, @ApiParam(value = "Gate id", required = true) @RequestParam @Valid Long gateId) {
        return gateService.insertAvailabilitiesForGate(availabilityDtos, gateId);
    }
    
    @DeleteMapping("/clearAvailability")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send DELETE request to add time availabilities to gate", httpMethod = "DELETE", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity deleteAvailabilitiesForGate(@ApiParam(value = "Gate id", required = true) @RequestParam @Valid Long gateId) {
        return gateService.deleteAvailabilities(gateId);
    }
    
    @GetMapping("/available")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send GET request to fetch first available gate", httpMethod = "GET", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity findAvailableGate() {
        return gateService.findAvailableGate();
    }
    
    

}
