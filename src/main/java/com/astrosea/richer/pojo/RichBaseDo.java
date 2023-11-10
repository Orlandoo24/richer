package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rich_reward_base")
public class RichBaseDo extends BaseEntity {

    /**
     * 当天矿场收益
     */
    private BigDecimal base;

    /**
     * 当天矿场收益（实际是 base * decBase
     */
    private BigDecimal rewBase;

    /**
     * 当天收益基数
     */
    private BigDecimal decBase;

    /**
     * 当天持有者数量
     */
    private Long curHolderNum;

    /**
     * 收益日期
     */
    private LocalDate rewData;

}