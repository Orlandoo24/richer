package com.astrosea.richer.service.impl;

import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.pojo.RichBaseDo;
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

    @Autowired
    OperatorLogMapper operatorMapper;

    /**
     * 1.填写当前的矿场收益
     * 2.计算当前所有用户的持有情况
     * 3.根据持有情况累加今天的收益
     * @param param
     * @return
     * @throws SQLException
     */
    @Transactional
    @Override
    public Response<UpdateGainsVo> updateGains(CreatGainParam param) throws SQLException {

        /**
         * 1.填写当前的矿场收益
         */
        // 获取精确到天的日期数据（今天
        LocalDate todayData = LocalDateTime.now().toLocalDate();
        // 获取 base
        BigDecimal base = BigDecimal.valueOf(param.getBase());
        // 校验今天有无更新过矿场产出数据
        RichBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichBaseDo.class)
                .eq(RichBaseDo::getRewData, todayData));
        if (baseDo != null) {
            return Response.successMsg(null, "already fill today");
        }

        Response response = updateBase(param.getBase(), param.getDecBase(), todayData);
        if (response.getCode() == MYSQL_ERROR_5001) {
            return response;
        }


        /**
         * 2.计算当前所有交了税用户的持有情况
         */
        List<HolderDto> holderList = nftMapper.getNftCountByRarity();

        System.out.println("持有者数量：" + holderList.size());
        for (HolderDto nftHolderDto : holderList) {


            BigDecimal curHolderReward = count(nftHolderDto, param.getDecBase());

            /**
             * 3.根据持有情况累加今天的收益
             * 先查看是否已有 holder 数据
             * 无则 insert，有则 update
             */
            // 根据当前领取人的地址直接搜寻持有者的奖励累加实体
            RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                    .eq(RichRewardLogDo::getAddress, nftHolderDto.getAddress()));

            // 数据更新
            int res = 0;
            if (logDo == null) {
                /**
                 * insert
                 */
                logDo = new RichRewardLogDo();

                // 构造收益数据
                logDo.setTotalReward(curHolderReward);

                // 添加收益更新时间数据
                logDo.setUpdateReward(todayData);
                logDo.setLatest(null);// 因为没有领取过所以没有最新领取地址
                // 添加钱包地址
                logDo.setAddress(nftHolderDto.getAddress());
                // 插入新收益日志
                res = logMapper.insert(logDo);
            } else {

                if (logDo.getUpdateReward().isEqual(todayData)) {
                    // 避免重复累加什么都不做
                    log.info("此用户今天的收益数据已经更新完毕，请勿反复累加");
                } else if (!logDo.getUpdateReward().isEqual(todayData)){
                    /**
                     * update
                     */
                    // 不为空，但收益数据更新的最新时间不是今天，则进行更新
                    log.info("不为空，但收益数据更新的最新时间不是今天，则进行更新");
                    res = logMapper.update(null, Wrappers.lambdaUpdate(RichRewardLogDo.class)
                            .set(RichRewardLogDo::getTotalReward, logDo.getTotalReward().add(curHolderReward))// 累加收益
                            .set(RichRewardLogDo::getUpdateReward, todayData)// 将收益累加时间更新为当前最新具体日期
                            .eq(RichRewardLogDo::getAddress, logDo.getAddress()));
                }
            }

        }

        /**
         * 构建当天的持有返回数据
         */
        UpdateGainsVo vo = new UpdateGainsVo();
        vo.setBase(param.getBase());
        vo.setDecBase(param.getDecBase());
        vo.setToday(todayData);
        vo.setHolderList(holderList);
        vo.setCurHolderNum(holderList.size());


        return Response.success(vo);
    }

    /**
     * 根据持有者实体更新收益数据
     * @param nftHolderDto
     * @param base
     * @return
     */
    public BigDecimal count(HolderDto nftHolderDto, BigDecimal base) {

        // 获取当前持有者的 nft 持有数据, 转为 BigDecimal
        BigDecimal lv1amt = BigDecimal.valueOf(nftHolderDto.getLv1amt());
        BigDecimal lv2amt = BigDecimal.valueOf(nftHolderDto.getLv2amt());
        BigDecimal lv3amt = BigDecimal.valueOf(nftHolderDto.getLv3amt());
        BigDecimal lv4amt = BigDecimal.valueOf(nftHolderDto.getLv4amt());
        BigDecimal lv5amt = BigDecimal.valueOf(nftHolderDto.getLv5amt());
        BigDecimal lv6amt = BigDecimal.valueOf(nftHolderDto.getLv6amt());

        // 计算当天的收益
        BigDecimal todayRew = lv1amt.multiply(LV1VALUE)
                .add(lv2amt.multiply(LV2VALUE))
                .add(lv3amt.multiply(LV3VALUE))
                .add(lv4amt.multiply(LV4VALUE))
                .add(lv5amt.multiply(LV5VALUE))
                .add(lv6amt.multiply(LV6VALUE))
                .multiply(base)
                .setScale(1, RoundingMode.HALF_UP);// 将收益制约为1位小数

        return todayRew;
    }

    /**
     * 填写当天的收益基数
     *
     * @param baseRew
     * @param decBase
     * @param now
     * @return
     * @throws SQLException
     */
    @Override
    public Response updateBase(Long baseRew, BigDecimal decBase, LocalDate now) throws SQLException {
        RichBaseDo baseDo = new RichBaseDo();

        // 构建基数时间
        baseDo.setRewData(now);

        // 构建 base 数据
        baseDo.setRewBase(Long.valueOf(baseRew));

        // 填写基数
        baseDo.setDecBase(decBase);


        // 记录 base 数据
        int baseRes = rewardBaseMapper.insert(baseDo);

        if (baseRes == 0) {
            return Response.error(MYSQL_ERROR_5001, "base update error");
        }

        return Response.success();
    }


}
