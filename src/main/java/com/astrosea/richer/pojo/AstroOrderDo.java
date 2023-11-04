package com.astrosea.richer.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("astro_order")
public class AstroOrderDo {
    
    /**
     * 主键id
     */
    private Long id;
    
    /**
     * 订单id
     */
    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    private Long orderId;
    
    /**
     * 用户地址
     */
    private String userUrl;
    
    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * mint 数量
     */
    private Integer mintAmount;
    
    /**
     * 支付状态{1、paying为正在支付、2、success（paid）为成功、3 received 为已到账}
     */
    private Integer payStatus;
    
    /**
     * 区块链交易id
     */
    private String txId;

    /**
     * nft 编号列表
     */
    private String nftList;

    /**
     * utxo hash 列表
     */
    private String utxoList;

    /**
     * 支付身份 0 OG、1 WL、2 PUB
     */
    private Integer style;

    /**
     * 支付源 origin
     */
    private String payOrigin;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime insertTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime = LocalDateTime.now();
}
