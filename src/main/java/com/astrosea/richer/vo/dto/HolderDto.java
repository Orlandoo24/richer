package com.astrosea.richer.vo.dto;

import lombok.Data;

@Data
public class HolderDto {

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 稀有度1 数量
     */
    private Integer lv1amt;

    /**
     * 稀有度 2 数量
     */
    private Integer lv2amt;

    /**
     * 稀有度 3
     */
    private Integer lv3amt;

    /**
     * 稀有度 4 数量
     */
    private Integer lv4amt;

    /**
     * 稀有度 5 数量
     */
    private Integer lv5amt;

    /**
     * 稀有度 6 数量
     */
    private Integer lv6amt;
}