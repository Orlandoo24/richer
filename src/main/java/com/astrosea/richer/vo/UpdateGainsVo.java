package com.astrosea.richer.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateGainsVo {

    /**
     * 收益数据
     */
    private Long base;

    /***
     * 矿厂产出日期
     */
    private LocalDate today;

}
