package com.astrosea.richer.pojo;

import com.astrosea.richer.utils.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rich_holder")
public class RichHolderDo extends BaseEntity {

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 稀有度1
     */
    private Integer lv1amt;

    /**
     * 稀有度2
     */
    private Integer lv2amt;

    /**
     * 稀有度3
     */
    private Integer lv3amt;

    /**
     * 稀有度4
     */
    private Integer lv4amt;

    /**
     * 稀有度5
     */
    private Integer lv5amt;

    /**
     * 稀有度6
     */
    private Integer lv6amt;
}