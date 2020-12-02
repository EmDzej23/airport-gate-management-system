/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
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
    @ApiModelProperty(hidden = true)
    private Long id;
    private Long gate;
    @NotBlank(message = "Airplane reference id can not be empty")
    private Long airplane;
    @NotBlank(message = "Flight number can not be empty")
    private String number;
}
