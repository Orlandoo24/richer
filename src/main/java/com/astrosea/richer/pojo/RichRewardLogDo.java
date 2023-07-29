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
@TableName("rich_reward_log")
public class RichRewardLogDo extends BaseEntity {

    /**
     * 用户钱包地址
     */
    private String address;

    /**
     * 累加收益，领取后置0
     */
    private BigDecimal totalReward;

    /**
     * 最后领取日期
     */
    private LocalDateTime latest;

}
