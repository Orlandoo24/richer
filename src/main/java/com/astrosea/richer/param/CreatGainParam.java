package com.astrosea.richer.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatGainParam {

    private Long base;

    private BigDecimal decBase;

    private String operator;

}
