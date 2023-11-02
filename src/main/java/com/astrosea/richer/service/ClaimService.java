package com.astrosea.richer.service;

import com.astrosea.richer.param.ClaimCheckParam;
import com.astrosea.richer.response.Response;

import java.math.BigDecimal;

public interface ClaimService {


    /**
     * 领取收益总接口
     * @return
     */
    Response claim(String address);

    Boolean claimJudger(String address);


    Long creatOrder(String address, BigDecimal gains);

    Response claimCheck(ClaimCheckParam param);

    Response chainClaim(String address, BigDecimal reward, String orderId);


}
