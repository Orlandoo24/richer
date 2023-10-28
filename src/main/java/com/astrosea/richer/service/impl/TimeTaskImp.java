package com.astrosea.richer.service.impl;

import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.pojo.RichRewardBaseDo;
import com.astrosea.richer.pojo.RichRewardLogDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.vo.UpdateGainsVo;
import com.astrosea.richer.vo.dto.HolderDto;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.astrosea.richer.constant.HttpCode.INNER_ERROR_5000;
import static com.astrosea.richer.constant.HttpCode.MYSQL_ERROR_5001;
import static com.astrosea.richer.constant.RichConstant.*;

@Slf4j
@Service
public class TimeTaskImp implements TimeTaskService {

    @Autowired
    TaxAllNftDoMapper nftMapper;

    @Autowired
    RichHolderMapper holderMapper;

    @Autowired
    RewardBaseMapper rewardBaseMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RichRewardLogMapper logMapper;

    @Autowired
    Tax1of1NftDoMapper of1NftDoMapper;


    @Transactional
    @Override
    public Response<UpdateGainsVo> updateGains(CreatGainParam param) throws SQLException {

        // 获取精确到天的日期数据（今天
        LocalDate todayData = LocalDateTime.now().toLocalDate();
        // 获取 base
        BigDecimal base = BigDecimal.valueOf(param.getBase());

        // 校验今天有无更新过矿场产出数据
        RichRewardBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichRewardBaseDo.class)
                .eq(RichRewardBaseDo::getRewData, todayData));
        if (baseDo != null) {
            return Response.successMsg(null, "already fill today");
        }

        /**
         * 更新当天的矿场收益数据
         */
        Response response = rewardCounter(param.getBase(), todayData);
        if (response.getCode() == MYSQL_ERROR_5001) {
            return response;
        }

        List<HolderDto> holderList = nftMapper.getNftCountByRarity();

        /**
         * 先查看是否已有 holder 数据
         * 无则 insert，有则 update
         */
        for (HolderDto nftHolderDto : holderList) {

            Integer commonAmt = nftHolderDto.getLv1amt() + nftHolderDto.getLv2amt() + nftHolderDto.getLv3amt() + nftHolderDto.getLv4amt() + nftHolderDto.getLv5amt();
            if (commonAmt == 0) {
                break;
            }

            // 获取当前持有者的 nft 持有数据, 转为 BigDecimal
            BigDecimal lv1amt = BigDecimal.valueOf(nftHolderDto.getLv1amt());
            BigDecimal lv2amt = BigDecimal.valueOf(nftHolderDto.getLv2amt());
            BigDecimal lv3amt = BigDecimal.valueOf(nftHolderDto.getLv3amt());
            BigDecimal lv4amt = BigDecimal.valueOf(nftHolderDto.getLv4amt());
            BigDecimal lv5amt = BigDecimal.valueOf(nftHolderDto.getLv5amt());
            BigDecimal lv6amt = BigDecimal.valueOf(nftHolderDto.getLv6amt());

            // 计算当天的收益
            BigDecimal todayRew = lv1amt.multiply(LV1P)
                    .add(lv2amt.multiply(LV2P))
                    .add(lv3amt.multiply(LV3P))
                    .add(lv4amt.multiply(LV4P))
                    .add(lv5amt.multiply(LV5P))
                    .add(lv6amt.multiply(LV6P))
                    .multiply(base)
                    .setScale(1, RoundingMode.HALF_UP);

            // 根据当前领取人的地址直接搜寻持有者实体
            RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                    .eq(RichRewardLogDo::getAddress, nftHolderDto.getAddress()));

            // 数据更新
            int res = 0;

            // 若还无今天的数据,更新新数据
            if (logDo == null) {
                logDo = new RichRewardLogDo();
                // 构造收益数据
                logDo.setTotalReward(todayRew);
                // 添加时间数据
                logDo.setLatest(todayData);
                // 添加钱包地址
                logDo.setAddress(nftHolderDto.getAddress());
                // 插入新收益日志
                res = logMapper.insert(logDo);
            } else if (logDo != null && !logDo.getUpdateReward().isEqual(todayData)){
                // 不为空，但收益数据更新的最新时间不是今天，则进行更新
                res = logMapper.update(null, Wrappers.lambdaUpdate(RichRewardLogDo.class)
                        .set(RichRewardLogDo::getTotalReward, logDo.getTotalReward().add(todayRew))// 累加收益
                        .set(RichRewardLogDo::getUpdateReward, todayData)// 将收益累加时间更新为当前具体日期
                        .eq(RichRewardLogDo::getAddress, logDo.getAddress()));
            } else if (logDo != null && logDo.getUpdateReward().isEqual(todayData)) {
                log.info("此用户今天的收益数据已经更新完毕，请勿反复累加");
                return Response.error(INNER_ERROR_5000, "此用户今天的收益数据已经更新完毕，请勿反复累加");
            }

            if (res == 0) {
                return Response.error(MYSQL_ERROR_5001, "update log data fail");
            }

        }

        UpdateGainsVo vo = new UpdateGainsVo();
        vo.setBase(param.getBase());
        vo.setToday(todayData);

        return Response.success(vo);
    }


    @Override
    public Response rewardCounter(Long baseRew, LocalDate now) throws SQLException {
        RichRewardBaseDo baseDo = new RichRewardBaseDo();

        // 塞时间
        baseDo.setRewData(now);

        // 构建 base 数据
        baseDo.setRewBase(Long.valueOf(baseRew));
        BigDecimal base = BigDecimal.valueOf(baseRew);

        // 记录 base 数据
        int baseRes = rewardBaseMapper.insert(baseDo);

        if (baseRes == 0) {
            return Response.error(MYSQL_ERROR_5001, "base update error");
        }

        return Response.success();
    }


}
