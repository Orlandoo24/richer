package com.astrosea.richer.param;

import lombok.Data;

@Data
public class ClaimCheckRequest {

    private Long orderId;

    private String address;

    private String reward;

}
