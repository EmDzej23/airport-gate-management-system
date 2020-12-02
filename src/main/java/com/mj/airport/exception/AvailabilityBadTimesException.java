/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.exception;

/**
 *
 * @author marko
 */
public class AvailabilityBadTimesException extends Exception {
    private final String message = "Start time must be before end time";

    @Override
    public String getMessage() {
        return message;
    }
    
}
