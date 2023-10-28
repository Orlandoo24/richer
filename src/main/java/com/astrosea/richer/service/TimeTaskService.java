package com.astrosea.richer.service;

import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.vo.UpdateGainsVo;

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
     * @param baseRew
     * @param now
     * @return
     */
    Response rewardCounter(Long baseRew, LocalDate now) throws SQLException;


}
