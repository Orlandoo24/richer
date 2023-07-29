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
@TableName("rich_reward_base")
public class RichRewardBaseDo extends BaseEntity {

    /**
     * 当天矿场收益
     */
    private BigDecimal rewBase;

    /**
     * 收益日期
     */
    private LocalDateTime rewData;

    /**
     * 等级1收益
     */
    private BigDecimal lv1;

    /**
     * 等级2收益
     */
    private BigDecimal lv2;

    /**
     * 等级3收益
     */
    private BigDecimal lv3;

    /**
     * 等级4收益
     */
    private BigDecimal lv4;

    /**
     * 等级5收益
     */
    private BigDecimal lv5;

    /**
     * 等级6收益
     */
    private BigDecimal lv6;
}