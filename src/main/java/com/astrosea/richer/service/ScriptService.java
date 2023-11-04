package com.astrosea.richer.service;

import com.astrosea.richer.param.ClaimParam;
import com.astrosea.richer.param.NftResenderParam;
import com.astrosea.richer.response.Response;

import java.math.BigDecimal;

public interface ScriptService {

    Response claim(ClaimParam param);

    Response nftResender(NftResenderParam param);

    /**
     * 创建订单数据并返回订单号
     *
     * @param nftId
     * @param address
     * @param cost
     * @param mintAmt
     * @param style
     * @param status
     * @return
     */
    Long creatResendOrder(Integer nftId, String address , BigDecimal cost , Integer mintAmt, Integer style, Integer status);

}
