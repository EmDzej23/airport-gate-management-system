/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.dto.LoginRequestDto;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author marko
 */
@Slf4j
public class AuthenticationControllerTest extends ControllerTest {

    @Test
    public void authenticateSuccessTest() {
        log.debug("authenticateSuccessTest");
        String jwtResponse = performSuccessfullLogin();

        String stringId = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtResponse).getBody().getSubject();
        log.debug("authenticateSuccessTest stringId " + stringId);
        Assert.assertEquals("Wrong user id returned", "1", stringId);
    }

    @Test
    public void authenticateFailureTest() {
        log.debug("authenticateFailureTest");
        HttpEntity request = new HttpEntity<>(new LoginRequestDto("user", "wrongPass"), headers());

        log.debug("authenticateFailureTest call " + authenticateURI + " with " + request);
        ResponseEntity jwtResponse = restTemplate.exchange(authenticateURI, HttpMethod.POST, request, Object.class);
        Assert.assertEquals("Wrong response status", HttpStatus.BAD_REQUEST.value(), jwtResponse.getStatusCodeValue());
        log.debug("authenticateFailureTest jwtResponse " + jwtResponse);
    }

}
