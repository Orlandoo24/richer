package com.astrosea.richer.service;

import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.vo.UpdateGainsVo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

public interface TimeTaskService {

    /**
     * 1.填入收益数据
     * 2.nft 总表扫描，获取所有的持有者数据
     * 3.再到 rich_reward_log 表中累加收益数据
     */
    Response<UpdateGainsVo> updateGains(CreatGainParam param) throws SQLException;

    /**
     * @param base
     * @param rewBase
     * @param realDecBase
     * @param today
     * @param curHolderNum
     * @return
     */
    Response updateBase(BigDecimal base, BigDecimal rewBase, BigDecimal realDecBase,LocalDate today, Long curHolderNum) throws SQLException;


    /**
     * 1.先校验今天是否已经更新，避免重复累加
     * 2.得出持有者数据
     * 3.根据持有者计算 base * decBase = 1.5 rewBase
     * 4.根据 1.5 rewBase 得出 2.4 rewBase
     */
    Response<UpdateGainsVo> timeTaskUpdateGains() throws SQLException;

    /**
     * @param base
     * @param decBase
     * @param now
     * @param curHolderNum
     * @param yesterdayBase
     * @return
     */
    Response timeTaskUpdateBase(BigDecimal base, BigDecimal decBase, LocalDate now, Long curHolderNum, BigDecimal yesterdayBase) throws SQLException;


    Response updateBaseRew(Integer newBaseRew);


}
