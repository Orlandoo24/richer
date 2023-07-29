package com.Astro.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * NFT WL 白名单购买日志表
 */
@Data
@TableName("astro_wl_pay_log")
public class AstroWlPayLogDo {

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
     * 支付时间
     */
    private String payTime;

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
}
