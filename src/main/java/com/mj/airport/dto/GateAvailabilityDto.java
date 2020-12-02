/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mj.airport.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
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
public class GateAvailabilityDto {
    
    @ApiModelProperty(hidden = true)
    private GateDto gateDto;
    private List<AvailabilityDto> availabilities;
}
