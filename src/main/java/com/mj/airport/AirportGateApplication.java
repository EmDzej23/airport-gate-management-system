package com.mj.airport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marko
 */
@SpringBootApplication
@EnableRetry
public class AirportGateApplication {
    //main
    public static void main(String[] args) {
        SpringApplication.run(AirportGateApplication.class, args);
    }
}
