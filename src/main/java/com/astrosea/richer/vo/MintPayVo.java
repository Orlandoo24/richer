package com.astrosea.richer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MintPayVo implements Serializable {

    /**
     * mint 支付金额
     */
    private Integer payAmount;

    /**
     * 可以 mint 的数量
     */
    private Integer curMintAmount;

    /**
     * 支付订单 id
     */
    private Long orderId;

}
