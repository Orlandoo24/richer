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
public class OrderDo extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

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
     * 状态
     */
    private Integer status;
}