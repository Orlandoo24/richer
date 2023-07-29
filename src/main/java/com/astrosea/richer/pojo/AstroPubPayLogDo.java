package com.Astro.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("astro_pub_pay_log")
public class AstroPubPayLogDo {

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
     * 公售购买数量
     */
    private Integer mintAmount;

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
