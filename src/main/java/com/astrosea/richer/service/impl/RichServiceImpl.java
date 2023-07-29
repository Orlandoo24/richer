package com.astrosea.richer.service.impl;


import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.FillBaseParam;
import com.astrosea.richer.param.RicherParam;
import com.astrosea.richer.pojo.RichHolderDo;
import com.astrosea.richer.pojo.RichOrderDo;
import com.astrosea.richer.pojo.RichRewardBaseDo;
import com.astrosea.richer.pojo.RichRewardLogDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.vo.GiveVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.astrosea.richer.constant.HttpCode.BIZ_ERROR;
import static com.astrosea.richer.constant.RichConstant.*;


@Service
public class RichServiceImpl implements RichService {

    @Autowired
    TaxAllNftDoMapper nftMapper;

    @Autowired
    RewardBaseMapper rewardBaseMapper;

    @Autowired
    RichOrderMapper orderMapper;


    @Autowired
    RichHolderMapper holderMapper;

    @Autowired
    RichRewardLogMapper logMapper;


    @Override
    public Boolean isRicher(String address) {
        if (address == null) {
            Response.error(BIZ_ERROR, "address cannot be null");
        }

        Boolean isRicher = false;

        // 再判断今天有无领取收益
        RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                .eq(RichRewardLogDo::getAddress, address));

        // 获取累加收益
        BigDecimal totalReward = logDo.getTotalReward();

        // 获取最新领取时间
        LocalDateTime latest = logDo.getLatest();

        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();

        // 判断latest的日期部分与当前日期的日期部分是否相等,相等则今天已经领取过
        boolean hadGet = latest.toLocalDate().isEqual(now.toLocalDate());

        // 有资格 & 今天没领过
        if (totalReward != BigDecimal.ZERO && !hadGet) {
            isRicher = true;
        }
        return isRicher;
    }

    /**
     * 查询获取累加收益
     * @param address
     * @return
     */
    @Override
    public BigDecimal reward(String address) {
        RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                .eq(RichRewardLogDo::getAddress, address));
        BigDecimal totalReward = logDo.getTotalReward();
        return totalReward;
    }

    @Override
    public RichOrderDo creatOrder(String address, BigDecimal rewAmt) {
        RichOrderDo order = new RichOrderDo();
        // 生成订单 id
        Long orderId = IdWorker.getId();
        order.setRichOrderId(orderId);

        // 收益领取人钱包地址
        order.setAddress(address);

        // 生成当前时间戳
        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        order.setOrderTime(now);

        // 填写当前订单需要发放的金额
        order.setRewardAmt(rewAmt);

        orderMapper.insert(order);

        return order;
    }

    @Override
    public Response<GiveVo> throwDollar(RicherParam param) {

        String address = param.getAddress();

        // 判断有无资格领取
        Boolean isRicher = isRicher(address);

        if (isRicher) {
            // 查询累加收益
            BigDecimal reward = reward(address);

            // 生成订单数据
            RichOrderDo orderDo = creatOrder(address, reward);

            // 返回订单数据给链上进行收益发放
            GiveVo giveVo = new GiveVo();
            giveVo.setAddress(orderDo.getAddress());
            giveVo.setOrderId(orderDo.getRichOrderId());
            giveVo.setRewAmt(orderDo.getRewardAmt());
            return Response.success(giveVo);
        } else {
            return Response.successMsg(null, "Received earnings or ineligible for earnings");
        }

    }

    @Override
    public Response<Boolean> giveCB(Long orderId) {

        int update = orderMapper.update(null, Wrappers.lambdaUpdate(RichOrderDo.class)
                .eq(RichOrderDo::getRichOrderId, orderId)
                .set(RichOrderDo::getStatus, 2));

        if (update == 0) {
            return Response.successMsg(null, "order update fail");
        }
        return Response.success();
    }

    @Override
    public RichRewardBaseDo rewardCounter(Integer baseRew, LocalDateTime now) {
        RichRewardBaseDo baseDo = new RichRewardBaseDo();

        // 塞时间
        baseDo.setRewData(now);

        // 构建 base 数据
        BigDecimal base = BigDecimal.valueOf(baseRew);
        baseDo.setRewBase(base);

        // 构建当天的收益
        BigDecimal lv1 = base.multiply(LV1).multiply(LV1P);
        BigDecimal lv2 = base.multiply(LV2).multiply(LV2P);
        BigDecimal lv3 = base.multiply(LV3).multiply(LV3P);
        BigDecimal lv4 = base.multiply(LV4).multiply(LV4P);
        BigDecimal lv5 = base.multiply(LV5).multiply(LV5P);
        BigDecimal lv6 = base.multiply(LV6).multiply(LV6P);

        // 塞当天的收益数据
        baseDo.setLv1(lv1);
        baseDo.setLv2(lv2);
        baseDo.setLv3(lv3);
        baseDo.setLv4(lv4);
        baseDo.setLv5(lv5);
        baseDo.setLv6(lv6);

        // 记录 base 数据
        int baseRes = rewardBaseMapper.insert(baseDo);

        if (baseRes == 0) {
            throw new RuntimeException("base update fail");
        }
        return baseDo;
    }

    @Override
    public Response rewUpdate(FillBaseParam base) {
        return null;
    }


    /**
     * 在这个例子中，
     * 使用了 Stream API 来对 for 循环进行了转换。
     * 首先，使用 logList.stream() 方法将 List 转换为 Stream。
     * 然后，使用 map 方法将每个元素映射为一个新元素。
     * 在这里，我们将每个 RichRewardLogDo 对象转换为一个新的 RichRewardLogDo 对象，并计算出当天的收益。
     * 如果该对象不满足条件，则返回 null。接着，使用 filter 方法过滤掉所有为 null 的元素。最后，使用 forEach 方法对每个元素进行操作，并将结果更新到数据库中。
     * @param param
     * @return
     */
    @Override
    public Response<Boolean> fillBase(FillBaseParam param) {
        LocalDateTime now = LocalDateTime.now();

        // 获取 base
        BigDecimal base = BigDecimal.valueOf(param.getBase());
        RichRewardBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichRewardBaseDo.class)
                .eq(RichRewardBaseDo::getRewData, now));
        if (baseDo != null) {
            return Response.successMsg(null, "already fill today");
        }

        // 生成当天的 base 收益数据
        baseDo = rewardCounter(param.getBase(), now);

        // 根据base数据计算当天的收益数据，全表扫描生成所有会员当天的累加收益数据
        List<RichRewardLogDo> logList = logMapper.selectList(null);
        logList.stream()
                .map(logDo -> {
                    String address = logDo.getAddress();

                    // 当前会员 nft 持有情况
                    RichHolderDo holder = holderMapper.selectOne(Wrappers.lambdaQuery(RichHolderDo.class)
                            .eq(RichHolderDo::getAddress, address));

                    // 持有超过一个 nft 则有资格领取收益
                    BigDecimal lv1amt = BigDecimal.valueOf(holder.getLv1amt());
                    BigDecimal lv2amt = BigDecimal.valueOf(holder.getLv2amt());
                    BigDecimal lv3amt = BigDecimal.valueOf(holder.getLv3amt());
                    BigDecimal lv4amt = BigDecimal.valueOf(holder.getLv4amt());
                    BigDecimal lv5amt = BigDecimal.valueOf(holder.getLv5amt());
                    BigDecimal lv6amt = BigDecimal.valueOf(holder.getLv6amt());

                    // 转成 BigDecimal
                    BigDecimal nftAmt = lv1amt.add(lv2amt)
                            .add(lv3amt)
                            .add(lv4amt)
                            .add(lv5amt)
                            .add(lv6amt);

                    // 如果累加个数等于0则返回null
                    if (nftAmt.compareTo(BigDecimal.ZERO) == 0) {
                        return null;
                    }

                    // 计算当天的收益
                    BigDecimal todayRew = lv1amt.multiply(LV1P)
                            .add(lv2amt.multiply(LV2P))
                            .add(lv3amt.multiply(LV3P))
                            .add(lv4amt.multiply(LV4P))
                            .add(lv5amt.multiply(LV5P))
                            .add(lv6amt.multiply(LV6P))
                            .multiply(base)
                            .setScale(1, RoundingMode.HALF_UP);

                    // 累加今天的收益
                    logDo.setLatest(now);
                    logDo.setTotalReward(logDo.getTotalReward().add(todayRew));
                    return logDo;
                })
                .filter(Objects::nonNull)
                .forEach(logDo -> {
                    int logUpdate = logMapper.update(null, Wrappers.lambdaUpdate(RichRewardLogDo.class)
                            .set(RichRewardLogDo::getTotalReward, logDo.getTotalReward())
                            .set(RichRewardLogDo::getLatest, logDo.getLatest())
                            .eq(RichRewardLogDo::getAddress, logDo.getAddress()));
                });
        return Response.success();
    }



}
