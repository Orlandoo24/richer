package com.astrosea.richer.vo;

import com.astrosea.richer.vo.dto.HolderDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateGainsVo {

    /**
     * 收益数据
     */
    private BigDecimal base;

    /**
     * 假收益基数
     */
    private BigDecimal fakeRewBase;

    /**
     * 真收益基数
     */
    private BigDecimal realRewBase;

    /**
     * 假收益基数
     */
    private BigDecimal fakeDecBase;

    /**
     * 真实收益基数
     */
    private BigDecimal realDecBase;

    /***
     * 矿厂产出日期
     */
    private LocalDate today;

    /**
     * 持有者实体
     */
    private List<HolderDto> holderList;

    /**
     * 当前持有者数量
     */
    private Integer curHolderNum;

}
