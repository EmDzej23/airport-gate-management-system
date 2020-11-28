/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author marko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {
    private Long id;
    private Long gate;
    private Long airplane;
    private String number;
}
