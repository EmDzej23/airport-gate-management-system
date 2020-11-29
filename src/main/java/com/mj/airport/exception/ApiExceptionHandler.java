/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.exception;

import com.mj.airport.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author marko
 */
@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {
    
    @Autowired
    FlightService service;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity handleValidationError(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String defaultMessage = fieldError.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorDto(
                        HttpStatus.BAD_REQUEST.value(),
                        defaultMessage
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleInternalServerError(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getSQLException().getMessage()
                ));
    }
    
//    @ExceptionHandler({StaleObjectStateException.class, ObjectOptimisticLockingFailureException.class})
//    @ResponseStatus(code = HttpStatus.OK)
//    public ResponseEntity handleInternalServerError(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        System.out.println(request.getParameter("number"));
//        return service.assignFlightToGate("number_11");
//    }
    
    
}
