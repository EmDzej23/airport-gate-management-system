/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.auth.JwtTokenProvider;
import com.mj.airport.auth.UserPrincipal;
import com.mj.airport.dto.LoginRequestDto;
import com.mj.airport.dto.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @ApiOperation(value = "Send POST request to perform authentication and obtain jwt token with entity parameters data", httpMethod = "POST", code = 200, response = Object.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authentication is successful"),
            @ApiResponse(code = 400, message = "Bad request, invalid input parameters. Error code: BAD_REQUEST"),
            @ApiResponse(code = 401, message = "Invalid client or credentials provided. Error code: INVALID_CLIENT or UNAUTHORIZED"),
            @ApiResponse(code = 500, message = "Runtime server error occured. Error code: SERVER_ERROR")})
    public ResponseEntity login(@RequestBody @Valid @ApiParam(value = "Model used for authentication", required = true) LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                String jwt = tokenProvider.generateToken(authentication);
                UserDto userDto = new UserDto(principal.getUser(), jwt);
                return ResponseEntity.ok(userDto);
            }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (AuthenticationException e) {
            log.warn("Login failed with reason - " + e.getMessage());
            return new ResponseEntity(Arrays.asList("User credentials can not be empty"), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/logout")
    @ApiOperation(value = "Send GET request to perform logout operation ", httpMethod = "GET", code = 200, response = Object.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Logout is successful"),
            @ApiResponse(code = 401, message = "Invalid client or credentials provided. Error code: INVALID_CLIENT or UNAUTHORIZED"),
            @ApiResponse(code = 500, message = "Runtime server error occured. Error code: SERVER_ERROR")})
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logout is successful");
    }

}

