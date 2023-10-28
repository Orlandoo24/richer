package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rich_reward_base")
public class RichRewardBaseDo extends BaseEntity {

    /**
     * 当天矿场收益
     */
    private Long rewBase;

    /**
     * 收益日期
     */
    private LocalDate rewData;

}