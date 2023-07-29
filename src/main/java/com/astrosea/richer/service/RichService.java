package com.astrosea.richer.service;


import com.astrosea.richer.param.FillBaseParam;
import com.astrosea.richer.param.RicherParam;
import com.astrosea.richer.pojo.RichOrderDo;
import com.astrosea.richer.pojo.RichRewardBaseDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.vo.GiveVo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RichService {

    Boolean isRicher(String address);
    BigDecimal reward(String address);
    RichOrderDo creatOrder(String address, BigDecimal rewAmt);
    Response giveCB(Long orderId);

    /**
     * 领取收益总接口
     * @return
     */
    Response<GiveVo> throwDollar(RicherParam param);


    /**
     * @param baseRew
     * @param now
     * @return
     */
    RichRewardBaseDo rewardCounter(Integer baseRew, LocalDateTime now);

    Response rewUpdate(FillBaseParam base);

    /**
     * 填写当天的矿场收益
     * @param base
     * @return
     */
    Response<Boolean> fillBase(FillBaseParam base);













}
