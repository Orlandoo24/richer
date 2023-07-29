package com.astrosea.richer.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GiveVo {

    private String address;

    private BigDecimal rewAmt;

    private Long orderId;

}
