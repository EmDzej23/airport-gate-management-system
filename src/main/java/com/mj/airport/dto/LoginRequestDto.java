package com.mj.airport.dto;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto implements Serializable {

    @ApiModelProperty(value = "Authentication username", required = true, example = "airport", position = 0)
    private String username;

    @ApiModelProperty(value = "Authentication password", required = true, example = "airport123", position = 1)
    private String password;

}
