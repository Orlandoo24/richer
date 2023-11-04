package com.astrosea.richer.param;

import lombok.Data;

@Data
public class NftResenderParam {

    /**
     * 补发的 nftId
     */
    private Integer nftId;

    private String address;

    private Integer cost;

    private String key;

}
