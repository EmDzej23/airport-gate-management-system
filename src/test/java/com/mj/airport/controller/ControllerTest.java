/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.controller;

import com.mj.airport.dto.LoginRequestDto;
import com.mj.airport.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author marko
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class ControllerTest {
    @Value("${security.jwt-secret}")
    String secret;

    @Autowired
    TestRestTemplate restTemplate;

    final String authenticateURI = "/auth/login";

    HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    
    public String performSuccessfullLogin() {
        log.debug("performSuccessfullLogin");
        HttpEntity request = new HttpEntity<>(new LoginRequestDto("user", "string"), headers());

        log.debug("performSuccessfullLogin call " + authenticateURI + " with " + request);
        ResponseEntity<UserDto> jwtResponse = restTemplate.exchange(authenticateURI, HttpMethod.POST, request, UserDto.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), jwtResponse.getStatusCodeValue());
        log.debug("performSuccessfullLogin jwtResponse " + jwtResponse);

        return jwtResponse.getBody().getToken();
    }
}
