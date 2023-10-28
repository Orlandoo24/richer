package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rich_reward_log")
public class RichRewardLogDo extends BaseEntity {

    /**
     * 用户钱包地址
     */
    private String address;

    /**
     * 累加收益，领取后置 0
     */
    private BigDecimal totalReward;

    /**
     * 收益最后累加时间
     */
    private LocalDate updateReward;

    /**
     * 最后领取日期
     */
    private LocalDate latest;

    /**
     * 最后领取具体时间
     */
    private LocalDateTime latestDetail;

}
