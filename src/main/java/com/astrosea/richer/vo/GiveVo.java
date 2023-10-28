package com.astrosea.richer.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GiveVo {

    // 钱包地址
    private String address;

    // 能领取的收益
    private BigDecimal amount;

    private Long orderId;

}
