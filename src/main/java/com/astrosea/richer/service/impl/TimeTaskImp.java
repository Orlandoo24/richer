package com.astrosea.richer.service.impl;

import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.pojo.RichBaseDo;
import com.astrosea.richer.pojo.RichRewardLogDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.utils.RandomUtil;
import com.astrosea.richer.vo.BaseRewVo;
import com.astrosea.richer.vo.UpdateGainsVo;
import com.astrosea.richer.vo.dto.HolderDto;
import com.astrosea.richer.vo.dto.TodayRewDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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


        // 获取精确到天的日期数据（今天
        LocalDate todayData = LocalDateTime.now().toLocalDate();

        // 获取 base
        BigDecimal fakeRewBase = param.getFakeRewBase();

        // 关系到今天收益的真实基数
        BigDecimal realDecBase = param.getRealDecBase();

        BigDecimal base = BigDecimal.ZERO;

        /**
         * 校验今天有无更新过矿场产出数据
         */
        RichBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichBaseDo.class)
                .eq(RichBaseDo::getRewData, todayData));
        if (baseDo != null) {
            return Response.successMsg(null, "already fill today");
        }


        /**
         * 1.计算当前所有交了税用户的持有情况
         */
        List<HolderDto> holderList = nftMapper.getNftCountByRarity();

        for (HolderDto nftHolderDto : holderList) {

            // 计算单个持有者的收益(按照接口的参数填写
            TodayRewDto dto = count(nftHolderDto, realDecBase);

            // 累加 base
            base = base.add(dto.getPersonalBase());

            // update 单个持有者收益数据 log
            Response response = personalClaimUpdate(nftHolderDto.getAddress(), dto.getPersonalRewBase(), todayData);
        }

        /**
         * 3.填写当前的矿场收益
         */
        Response response = updateBase(base , fakeRewBase, realDecBase, todayData, Long.valueOf(holderList.size()));
        if (response.getCode() == MYSQL_ERROR_5001) {
            return response;
        }


        /**
         * 构建当天的持有返回数据
         */
        UpdateGainsVo vo = new UpdateGainsVo();
        vo.setBase(base);
        vo.setFakeDecBase(param.getFakeRewBase());
        vo.setRealRewBase(base.multiply(realDecBase));
        vo.setRealDecBase(realDecBase);
        vo.setFakeDecBase(realDecBase);
        vo.setToday(todayData);
        vo.setHolderList(holderList);
        vo.setCurHolderNum(holderList.size());

        return Response.success(vo);
    }

    /**
     * 计算单个持有者的收益
     * @param nftHolderDto
     * @param realDecBase
     * @return
     */
    public TodayRewDto count(HolderDto nftHolderDto, BigDecimal realDecBase) {

        // 获取当前持有者的 nft 持有数据, 转为 BigDecimal
        BigDecimal lv1amt = BigDecimal.valueOf(nftHolderDto.getLv1amt());
        BigDecimal lv2amt = BigDecimal.valueOf(nftHolderDto.getLv2amt());
        BigDecimal lv3amt = BigDecimal.valueOf(nftHolderDto.getLv3amt());
        BigDecimal lv4amt = BigDecimal.valueOf(nftHolderDto.getLv4amt());
        BigDecimal lv5amt = BigDecimal.valueOf(nftHolderDto.getLv5amt());
        BigDecimal lv6amt = BigDecimal.valueOf(nftHolderDto.getLv6amt());

        // 计算当天的收益
        BigDecimal todayBase = lv1amt.multiply(LV1VALUE)
                .add(lv2amt.multiply(LV2VALUE))
                .add(lv3amt.multiply(LV3VALUE))
                .add(lv4amt.multiply(LV4VALUE))
                .add(lv5amt.multiply(LV5VALUE))
                .add(lv6amt.multiply(LV6VALUE))
                .setScale(1, RoundingMode.HALF_UP);// 将收益制约为1位小数

        BigDecimal todayRealBase = todayBase.multiply(realDecBase)
                .setScale(1, RoundingMode.HALF_UP);// 将收益制约为1位小数

        TodayRewDto dto = new TodayRewDto();

        dto.setPersonalBase(todayBase);
        dto.setPersonalRewBase(todayRealBase);

        return dto;
    }

    /**
     * 填写当天的收益基数
     *
     * @param base
     * @param rewBase
     * @param now
     * @param curHolderNum
     * @return
     * @throws SQLException
     */
    @Override
    public Response updateBase(BigDecimal base, BigDecimal rewBase, BigDecimal realDecBase, LocalDate now, Long curHolderNum) throws SQLException {
        RichBaseDo baseDo = new RichBaseDo();

        // 构建用户总收益数据
        baseDo.setBase(base);

        // 构建真实收益发放数据
        baseDo.setRewBase(rewBase);

        // 填写倍数基数
        baseDo.setDecBase(realDecBase);

        // 当前持有者数量
        baseDo.setCurHolderNum(curHolderNum);

        // 构建基数时间
        baseDo.setRewData(now);

        // 记录 base 数据
        int baseRes = rewardBaseMapper.insert(baseDo);

        if (baseRes == 0) {
            return Response.error(MYSQL_ERROR_5001, "base update error");
        }

        return Response.success();
    }

    /**
     *  定时任务全自动化更新收益
     * 1.计算当前所有用户的持有情况
     * 2.根据持有情况累加得到总收益 rewBase（总收益base当作矿场收益） ， base  * decBase = rewBase
     * 3.更新当天的矿场收益
     * @return
     * @throws SQLException
     */
    @Transactional
    @Override
    public Response<UpdateGainsVo> timeTaskUpdateGains() throws SQLException {

        // 获取精确到天的日期数据（今天
        LocalDate todayData = LocalDateTime.now().toLocalDate();

        // 初始化总收益
        BigDecimal rewBase = BigDecimal.ZERO;

        // 初始化总收益
        BigDecimal base = BigDecimal.ZERO;

        // 初始化倍数基数
        BigDecimal realDecBase = DECBASE_1_5;// 真实收益基数 1.5
        BigDecimal fakeDecBase = DECBASE_2_4;// 用于展示的假基数 2.4


        /**
         * 1.计算当前所有交了税用户的持有情况
         */
        List<HolderDto> holderList = nftMapper.getNftCountByRarity();

        /**
         * 2.遍历所有持有者，根据持有情况累加今天的收益
         * 先查看是否已有 holder 数据
         * 无则 insert，有则 update
         * 并且避免重复累加
         */

        for (HolderDto nftHolderDto : holderList) {

            // 得到个人当天能获得的收益
            TodayRewDto dto = timeTaskCount(nftHolderDto, DECBASE_1_5);// 定时任务计算真实收益

            // 累加最初收益
            base = base.add(dto.getPersonalBase());

            // 累加总收益
            rewBase = rewBase.add(dto.getPersonalRewBase());

            // update 单个持有者收益数据 log
            Response response = personalClaimUpdate(nftHolderDto.getAddress(), dto.getPersonalRewBase(), todayData);

        }


        /**
         * 3.填写当前的矿场收益
         */
        // 校验今天有无更新过矿场产出数据
        RichBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichBaseDo.class)
                .eq(RichBaseDo::getRewData, todayData));
        if (baseDo != null) {
            return Response.successMsg(null, "already fill today");
        }

        BigDecimal lastBaseRew = getLastBaseRew();// 获取最新的一条收益

        Response response = timeTaskUpdateBase(base , DECBASE_2_4 , todayData, Long.valueOf(holderList.size()), lastBaseRew);
        if (response.getCode() == MYSQL_ERROR_5001) {
            return response;
        }


        /**
         * 构建当天的持有返回数据
         */
        UpdateGainsVo vo = new UpdateGainsVo();
        vo.setBase(base);
        vo.setFakeRewBase(base.multiply(DECBASE_2_4));
        vo.setRealRewBase(rewBase);
        vo.setFakeDecBase(fakeDecBase);
        vo.setRealDecBase(realDecBase);
        vo.setToday(todayData);
        vo.setHolderList(holderList);
        vo.setCurHolderNum(holderList.size());

        return Response.success(vo);
    }

    public BigDecimal getLastBaseRew() {


        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 转换为精确到天的日期数据
        LocalDate today = now.toLocalDate();


        QueryWrapper<RichBaseDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("rew_base")
                .eq("is_deleted", false)
                .orderByDesc("rew_data")
                .last("LIMIT 1");

        RichBaseDo baseDo = rewardBaseMapper.selectOne(queryWrapper);

        if (baseDo == null) {

            return new BigDecimal(1888);
        }


        return baseDo.getRewBase();
    }

    /**
     * 计算单个持有者的收益
     *
     * @param nftHolderDto
     * @param decBase
     * @return
     */
    public TodayRewDto timeTaskCount(HolderDto nftHolderDto, BigDecimal decBase) {

        TodayRewDto dto = new TodayRewDto();

        // 获取当前持有者的 nft 持有数据, 转为 BigDecimal
        BigDecimal lv1amt = BigDecimal.valueOf(nftHolderDto.getLv1amt());
        BigDecimal lv2amt = BigDecimal.valueOf(nftHolderDto.getLv2amt());
        BigDecimal lv3amt = BigDecimal.valueOf(nftHolderDto.getLv3amt());
        BigDecimal lv4amt = BigDecimal.valueOf(nftHolderDto.getLv4amt());
        BigDecimal lv5amt = BigDecimal.valueOf(nftHolderDto.getLv5amt());
        BigDecimal lv6amt = BigDecimal.valueOf(nftHolderDto.getLv6amt());


        // 计算当天展示基础收益 base （无基数
        BigDecimal todayPersonalBase = lv1amt.multiply(LV1VALUE)
                .add(lv2amt.multiply(LV2VALUE))
                .add(lv3amt.multiply(LV3VALUE))
                .add(lv4amt.multiply(LV4VALUE))
                .add(lv5amt.multiply(LV5VALUE))
                .add(lv6amt.multiply(LV6VALUE))
                .setScale(1, RoundingMode.HALF_UP);// 将收益制约为1位小数

        // 个人真实收益 rewBase （加基数
        BigDecimal todayPersonalRewBase = todayPersonalBase.multiply(decBase)
                .setScale(1, RoundingMode.HALF_UP);// 将收益制约为1位小数


        dto.setPersonalBase(todayPersonalBase);
        dto.setPersonalRewBase(todayPersonalRewBase);

        return dto;
    }

    /**
     * 填写当天的收益基数
     *
     * @param base
     * @param decBase
     * @param now
     * @param curHolderNum
     * @param yesterdayBase
     * @return
     * @throws SQLException
     */
    @Override
    public Response timeTaskUpdateBase(BigDecimal base, BigDecimal decBase, LocalDate now, Long curHolderNum, BigDecimal yesterdayBase) throws SQLException {
        RichBaseDo baseDo = new RichBaseDo();

        // 构建用户总收益数据
        baseDo.setBase(base);

        // 构建实际收益发放数据（若填的是 fakeDecBase 则是飘高的
//        BigDecimal FAKE_1000 = new BigDecimal("1000.0");

        BigDecimal bigDecimal_10 = new BigDecimal("10.0");
        int randomNum = RandomUtil.getRandomNum();// 个位数
        log.warn("randomNum ：{}", randomNum);

        BigDecimal newBaseRew = yesterdayBase.add(bigDecimal_10).add(BigDecimal.valueOf(randomNum));// 最新的一条收益加十几
        baseDo.setRewBase(newBaseRew);

        // 填写倍数基数
        baseDo.setDecBase(decBase);

        // 当前持有者数量
        baseDo.setCurHolderNum(curHolderNum);

        // 构建基数时间
        baseDo.setRewData(now);

        // 记录 base 数据
        int baseRes = rewardBaseMapper.insert(baseDo);

        if (baseRes == 0) {
            return Response.error(MYSQL_ERROR_5001, "base update error");
        }

        return Response.success();
    }

    /**
     * 2.根据持有情况累加今天的收益
     * 先查看是否已有 holder 数据
     * 无则 insert，有则 update
     */
    public Response personalClaimUpdate(String address, BigDecimal curHolderReward, LocalDate todayData) {

        // 根据当前领取人的地址直接搜寻持有者的奖励累加实体
        RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                .eq(RichRewardLogDo::getAddress, address));

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
            logDo.setAddress(address);
            // 插入新收益日志
            res = logMapper.insert(logDo);
        } else {

            if (logDo.getUpdateReward().isEqual(todayData)) {// 避免重复累加, 什么都不做

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
                        .eq(RichRewardLogDo::getAddress, address));
            }
        }

        return Response.success();
    }

    @Override
    public Response updateBaseRew(Integer newBaseRew) {

        BaseRewVo vo = new BaseRewVo();

        UpdateWrapper<RichBaseDo> wrapper = new UpdateWrapper<>();
        wrapper.set("rew_base", newBaseRew)
                .eq("is_deleted", false)
                .orderByDesc("rew_data")
                .last("LIMIT 1");


        int update = rewardBaseMapper.update(null, wrapper);

        if (update == 0) {
            return Response.successMsg("update fail!");
        }


        return Response.success();
    }




}
