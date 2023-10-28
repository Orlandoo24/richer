package com.astrosea.richer.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimCheckParam {

    private Long orderId;

    private String address;

    private BigDecimal reward;

}
