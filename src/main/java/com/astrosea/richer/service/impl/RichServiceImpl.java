package com.astrosea.richer.service.impl;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.constant.HttpCode;
import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.ClaimCheckParam;
import com.astrosea.richer.param.FillBaseParam;
import com.astrosea.richer.param.GetCoinsParam;
import com.astrosea.richer.param.QueryCoinsParam;
import com.astrosea.richer.pojo.OrderDo;
import com.astrosea.richer.pojo.RichRewardBaseDo;
import com.astrosea.richer.pojo.RichRewardLogDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.utils.SHA256Util;
import com.astrosea.richer.vo.GiveVo;
import com.astrosea.richer.vo.QueryCoinsVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.astrosea.richer.constant.HttpCode.*;
import static com.astrosea.richer.constant.OrderConstant.*;

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
     * 单纯填写今天的收益数据
     * @param
     * @return
     */
    @Transactional
    @Override
    public Response<RichRewardBaseDo> fillBase(FillBaseParam param) {
        // 转换为精确到天的日期数据
        LocalDate today = LocalDateTime.now().toLocalDate();
        // 获取 base
        RichRewardBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichRewardBaseDo.class)
                .eq(RichRewardBaseDo::getRewData, today));
        if (baseDo != null) {
            return Response.successMsg(null, "already fill today");
        }

        /**
         * 更新当天的矿场收益数据
         */
        baseDo = new RichRewardBaseDo();
        baseDo.setRewBase(Long.valueOf(param.getBase()));
        baseDo.setRewData(today);
        int insert = rewardBaseMapper.insert(baseDo);
        if (insert == 0) {
            return Response.error(HttpCode.MYSQL_ERROR_5001, "fillBase database error");
        }

        return Response.success(baseDo);
    }


    /**
     * 判断当前用户能不能领取收益
     * @param address
     * @return
     */
    @Override
    public Boolean claimJudger(String address) {
        if (address == null) {
            Response.error(BIZ_ERROR, "address cannot be null");
        }

        Boolean couldClaim = false;

        // 获取累加表实体数据
        RichRewardLogDo logDo = logMapper.selectOne(Wrappers.lambdaQuery(RichRewardLogDo.class)
                .eq(RichRewardLogDo::getAddress, address));

        // 获取累加收益
        BigDecimal totalReward = logDo.getTotalReward();

        // 获取最新领取时间
        LocalDate latest = logDo.getLatest();

        // 获取当前日期和时间
        LocalDate today = LocalDateTime.now().toLocalDate();

        // 持有 nft && 当前的收益不为 0 && 最新的领取时间 && 不是今天
        if (!totalReward.equals(BigDecimal.ZERO) && !latest.isEqual(today)) {
            couldClaim = true;
        }
        return couldClaim;
    }




    /**
     * 收益领取总接口
     * @param param
     * @return
     */
    @Transactional
    @Override
    public Response claim(GetCoinsParam param) {

        String address = param.getAddress();

        // 判断有无资格领取
        Boolean couldClaim = claimJudger(address);

        if (couldClaim) {
            // 收益数
            BigDecimal reward = reward(address);

            // 订单 id
            Long orderId = creatOrder(address, reward);

            // 构建收益数据
            GiveVo giveVo = new GiveVo();
            giveVo.setAddress(address);// 收益接收人
            giveVo.setAmount(reward);// 收益数
            giveVo.setOrderId(orderId);

            // 构建盐
            String json = JSONUtil.toJsonStr(giveVo);
            String salt = "CFIrxG7nDq4h2TofxTGlmm220E7UI2JBxf";

            // json 加盐
            String jsonSalt = json + salt;

            // 256 盐
            String hash = SHA256Util.encrypt(jsonSalt);

            // 构造请求体 jsonObj
            JSONObject requestBody = new JSONObject();
            requestBody.set("address", address);
            requestBody.set("amount", reward);
            requestBody.set("hash", hash);
            requestBody.set("orderId", orderId);


            // 构造链上 url
            String chainUrl = "http://154.221.27.158:3000/claim";

            try {
            // 发送 http 请求给链上
                HttpResponse response = HttpUtil.createPost(chainUrl)
                        .body(requestBody.toString())// 请求体
                        .timeout(30000)// 设置超时时间（单位：毫秒）
                        .execute();
                // 打印返回结果日志
                log.info("请求链上接口响应结果 : {}", response);

                // 响应结果判断
                int status = response.getStatus();



                if (status == 200) {

                    Long responseOrderId = Long.valueOf(requestBody.get("orderId").toString());

                    int update = orderMapper.update(null, Wrappers.lambdaUpdate(OrderDo.class)
                            .eq(OrderDo::getRichOrderId, responseOrderId)
                            .set(OrderDo::getStatus, CHECKING_2));

                } else {
                    switch (status) {
                        case 400:
                            // 传参错误
                            log.info("传参错误");
                        case 401:
                            // 认证不通过
                            log.info("认证不通过");
                        case 403:
                            // ip 不在白名单
                            log.info("ip 不在白名单");
                        case 500:
                            // 服务器无法处理
                            log.info("服务器无法处理");
                            break;
                        default:
                            // 收益领取未知错误
                            log.info("收益领取未知错误");
                    }
                    log.info("收益领取错误:{}" ,response);
                    return Response.error(1000, "claim error");
                }
                
                // 返回领取成功的响应
                return Response.success(response);
            } catch (HttpException e) {
                // 处理 Http 请求发生异常的情况
                e.printStackTrace();
                log.info("请求链上接口响应结果异常 : {}", e);
                return Response.error(HttpCode.BIZ_ERROR, e.toString());
            }

        } else {
            return Response.successMsg(null, "Received earnings or ineligible for earnings");
        }

    }

    @Override
    public Long creatOrder(String address, BigDecimal gains) {

        OrderDo orderDo = new OrderDo();
        Long orderId = IdWorker.getId();
        // 获取最新领取时间
        // 获取当前日期和时间
        LocalDateTime current = LocalDateTime.now();

        orderDo.setRichOrderId(orderId);
        orderDo.setAddress(address);
        orderDo.setOrderTime(current);
        orderDo.setStatus(GIVING_1);

        int insert = orderMapper.insert(orderDo);

        if (insert == 0) {
            return -1L;
        }

        return orderId;
    }

    @Override
    public Response claimCheck(ClaimCheckParam param) {

        OrderDo orderDo = orderMapper.selectOne(Wrappers.lambdaQuery(OrderDo.class)
                .eq(OrderDo::getRichOrderId, param.getOrderId()));

        int orderRes = 0;
        int logUpdate = 0;
        if (orderDo != null && orderDo.getAddress().equals(param.getAddress()) && orderDo.getRewardAmt().equals(param.getReward())){
                orderRes = orderMapper.update(null, Wrappers.lambdaUpdate(OrderDo.class)
                        .eq(OrderDo::getRichOrderId, param.getOrderId())
                        .set(OrderDo::getStatus, CHECKED_3));

            // 成功:将当前用户的累计收益置 0 , 并且将最新领取时间置为今天
            logUpdate = logMapper.update(null, Wrappers.lambdaUpdate(RichRewardLogDo.class)
                    .eq(RichRewardLogDo::getAddress, orderDo.getAddress())
                    .set(RichRewardLogDo::getTotalReward, BigDecimal.ZERO)// 收益数清零
                    .set(RichRewardLogDo::getLatest, LocalDateTime.now().toLocalDate()));// 最新领取时间更新为今天
        } else {
            return Response.error(INNER_ERROR_5000, "当前收益发放不合法");
        }

        if (orderRes == 0 || logUpdate == 0) {
            return Response.error(MYSQL_ERROR_5001, "校验收益订单数据库错误");
        }

        return Response.success();
    }

    /**
     * 领取收益时查询个人累加收益
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
