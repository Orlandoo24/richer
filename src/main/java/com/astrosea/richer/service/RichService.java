package com.astrosea.richer.service;


import com.astrosea.richer.param.ClaimCheckParam;
import com.astrosea.richer.param.FillBaseParam;
import com.astrosea.richer.param.GetCoinsParam;
import com.astrosea.richer.param.QueryCoinsParam;
import com.astrosea.richer.pojo.RichRewardBaseDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.vo.QueryCoinsVo;

import java.math.BigDecimal;

public interface RichService {


    /**
     * 1. 填写当天的矿场收益
     *
     * @param param
     * @return
     */
    Response<RichRewardBaseDo> fillBase(FillBaseParam param);

    Response<QueryCoinsVo> query(QueryCoinsParam param);


    Boolean claimJudger(String address);



    /**
     * 领取收益总接口
     * @return
     */
    Response claim(GetCoinsParam param);


    Long creatOrder(String address, BigDecimal gains);

    Response claimCheck(ClaimCheckParam param);




























}
