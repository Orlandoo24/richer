package com.astrosea.richer.param;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimParam {

    // 钱包地址
    private String address;

    // 能领取的收益
    private BigDecimal amount;

}
