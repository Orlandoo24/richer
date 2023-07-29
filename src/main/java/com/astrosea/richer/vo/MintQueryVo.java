package com.astrosea.richer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MintQueryVo implements Serializable {

    /**
     * 可以 mint 的数量
     */
    private Integer mintAmount;

}
