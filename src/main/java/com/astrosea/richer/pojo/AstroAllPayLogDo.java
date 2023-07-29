package com.Astro.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * NFT 购买总表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("astro_all_pay_log")
public class AstroAllPayLogDo {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户url
     */
    private String userUrl;

    /**
     * 支付金额
     */
    private Integer payAmount;

    /**
     * 购买数量
     */
    private Integer mintAmount;

    /**
     * 用户类型：0 为public 、1为 og & wl 、2 为 og 、3为wl
     */
    private String userType;

    /**
     * 其他字段
     */
    private String otherJson;

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

//    @TableField(insertStrategy = FieldStrategy.IGNORED)
//    private LocalDateTime insertTime;
//
//    @TableField(insertStrategy = FieldStrategy.IGNORED)
//    private LocalDateTime updateTime;

}
