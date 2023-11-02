package com.astrosea.richer.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("claim_operator_log")
public class OperatorLogDo extends BaseEntity {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 当天操作情况的 JSON
     */
    private String logJson;

    /**
     * 日志状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime insertTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除，0：正常，1：删除
     */
    private Boolean deleted;
    
}
