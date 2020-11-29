/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.auth.Constants;
import com.mj.airport.dto.AvailabilityDto;
import com.mj.airport.service.GateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    GateService gateService;

     
    
    @PutMapping("/update")
    @Secured(Constants.ADMIN)
    @ApiOperation(value = "Send PUT request to update certain availability by id", httpMethod = "PUT", code = 200, authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity update(@ApiParam(value = "AvailabilityDto model", required = true) @RequestBody @Valid AvailabilityDto availabilityDto, @ApiParam(value = "Availability id", required = true) @RequestParam @Valid Long availabilityId) {
        return gateService.updateAvailability(availabilityDto, availabilityId);
    }    
    
    
}
