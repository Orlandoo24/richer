package com.astrosea.richer.vo.dto;

import lombok.Data;

@Data
public class NftResenderDto {

    private String orderId;

    private String from;

    private String nftUtxo;

    private String toAddress;

}