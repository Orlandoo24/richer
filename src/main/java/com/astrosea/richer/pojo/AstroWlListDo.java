package com.Astro.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * Astro 授权 WL 表
 */
@Data
@TableName("astro_wl_list")
public class AstroWlListDo {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 url
     */
    private String userUrl;

    /**
     * 是否使用
     * 0：未使用，1：mint 已支付，2: 狗🐶币已经上链到账
     */
    private Integer isUsed;

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
