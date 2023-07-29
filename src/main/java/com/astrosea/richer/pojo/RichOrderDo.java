package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rich_order")
public class RichOrderDo extends BaseEntity {

    /**
     * 订单ID
     */
    private Long richOrderId;

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 订单时间
     */
    private LocalDateTime orderTime;

    /**
     * 奖励数量
     */
    private BigDecimal rewardAmt;

    /**
     * 稀有度1
     */
    private Boolean lv1amt;

    /**
     * 稀有度2
     */
    private Boolean lv2amt;

    /**
     * 稀有度3
     */
    private Boolean lv3amt;

    /**
     * 稀有度4
     */
    private Boolean lv4amt;

    /**
     * 稀有度5
     */
    private Boolean lv5amt;

    /**
     * 稀有度6
     */
    private Boolean lv6amt;

    /**
     * 状态
     */
    private Integer status;
}