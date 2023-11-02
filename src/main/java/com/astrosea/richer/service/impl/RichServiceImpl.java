package com.astrosea.richer.service.impl;

import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.QueryCoinsParam;
import com.astrosea.richer.pojo.RichRewardLogDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.vo.QueryCoinsVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class RichServiceImpl implements RichService {

    @Autowired
    TaxAllNftDoMapper nftMapper;

    @Autowired
    RewardBaseMapper rewardBaseMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RichHolderMapper holderMapper;

    @Autowired
    RichRewardLogMapper logMapper;



    /**
     * 有资格的情况查询个人累加收益
     * @param address
     * @return
     */
    public BigDecimal reward(String address) {
        RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                .eq(RichRewardLogDo::getAddress, address));

        if (logDo == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalReward = logDo.getTotalReward();
        return totalReward;
    }

    /**
     * 用户查询收益
     * @param param
     * @return
     */
    @Override
    public Response<QueryCoinsVo> query(QueryCoinsParam param) {

        RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                .eq(RichRewardLogDo::getAddress, param.getAddress()));

        /**
         * 当前用户不在收益领取范围内
          */
        if (logDo == null) {
            return Response.error(403, "No current user");
        }

        BigDecimal rew = logDo.getTotalReward();
        QueryCoinsVo vo = new QueryCoinsVo();
        vo.setCoins(rew.toString());
        return Response.success(vo);
    }



}
