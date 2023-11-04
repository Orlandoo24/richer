package com.astrosea.richer.service.impl;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.mapper.*;
import com.astrosea.richer.param.ClaimCheckParam;
import com.astrosea.richer.pojo.OrderDo;
import com.astrosea.richer.pojo.RichRewardLogDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.ClaimService;
import com.astrosea.richer.utils.SHA256Util;
import com.astrosea.richer.vo.GiveVo;
import com.astrosea.richer.vo.dto.ClaimStatusDto;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.astrosea.richer.constant.HttpCode.BIZ_ERROR;
import static com.astrosea.richer.constant.HttpCode.CLAIM_NOT_ALLOW_5002;
import static com.astrosea.richer.constant.OrderConstant.CHECKED_3;
import static com.astrosea.richer.constant.OrderConstant.GIVING_1;


@Slf4j
@Service
public class ClaimImp implements ClaimService {

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
     * 领取收益接口
     * 1.判断有无资格，无则直接返回 msg
     * 2.有资格则计算收益有多少
     * 3.请求链上接口
     * 4.根据状态码返回响应信息 msg 和 状态码 statusCode
     *
     * @param address
     * @return
     */
    @Transactional
    @Override
    public Response claim(String address) {

        /**
         * 1.判断有无资格，无则直接返回 msg
         */
        Boolean couldClaim = claimJudger(address);

        BigDecimal reward = BigDecimal.ZERO;// 初始化收益值

        if (!couldClaim) {
            return Response.successMsg(null, "received earnings or no rights for earnings");
        } else {
             reward = reward(address);// 获取收益数据
        }

        /**
         * 3.请求链上接口
         * 4.根据状态码返回响应信息 msg 和 状态码 statusCode
         */
        String orderIdStr = creatOrder(address, reward).toString();// 创建订单id , 订单状态为
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Response response = chainClaim(address, reward, orderIdStr);// 请求链上获取收益

        return response;
    }

    /**
     * 1.判断当前用户能不能领取收益
     *
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
        // 累加收益数据为空
        if (logDo == null) {
            return false;
        }

        // 获取当前日期和时间
        LocalDate today = LocalDateTime.now().toLocalDate();
        // 获取累加收益
        BigDecimal totalReward = logDo.getTotalReward();

        // 获取累加收益日期是昨天
        LocalDate latestUpdateReward = logDo.getUpdateReward();

        // 累加收益为 0
        if (totalReward.equals(BigDecimal.ZERO)) {
            return false;
        }

        // 之前从没有没有领取过的情况
        LocalDate latest = logDo.getLatest();
        if (logDo != null && latest == null) {
            return true;
        }

        // 获取最新领取时间是今天则不能再领取了
        if (latest.isEqual(today)) {
            return false;
        }

        // 持有 nft && 当前的收益不为 0 && 最新的领取时间 && 不是今天
        if (!totalReward.equals(BigDecimal.ZERO) && latest.isBefore(today)) {
            couldClaim = true;
        }

        return couldClaim;
    }

    /**
     * 3.请求领取链接链上接口
     * 4.根据状态码返回响应信息 msg 和 状态码 statusCode
     * 5.若请求异常则返回报错堆栈 e 和  状态码 statusCode
     *
     * 整个请求都用 try 包上，链上报错也返回200，但会在 msg 中返回状态码中
     * @param address
     * @param reward
     * @param orderIdStr
     * @return
     */
    @Override
    public Response chainClaim(String address, BigDecimal reward, String orderIdStr) {

        String responseStr = "";
        int statusCode = 0;

        // 构建收益数据
        GiveVo giveVo = new GiveVo();
        giveVo.setAddress(address);// 地址
        giveVo.setAmount(reward);// 收益
        giveVo.setOrderId(orderIdStr);// 订单 idStr

        // 构建盐
        String json = JSONUtil.toJsonStr(giveVo);
        System.out.println("json:"+json);
        String salt = "CFIrxG7nDq4h2TofxTGlmm220E7UI2JBxf";
        // json 加盐
        String jsonSalt = json + salt;
        // 256 盐
        String sha = SHA256Util.encrypt(jsonSalt);

        try {
            // 构造请求体 jsonObj
            JSONObject requestBody = new JSONObject();
            requestBody.set("address", address);
            requestBody.set("amount", reward);
            requestBody.set("orderId", orderIdStr);
            requestBody.set("sha", sha);

            System.out.println("requestBody:"+requestBody);
            System.out.println("amount"+reward);
            System.out.println("请求体 orderId" + orderIdStr);
            System.out.println("sha"+sha);
            // 构造 http 请求 url
            String chainUrl = "http://94.130.49.158:3000/claim";// 线上
//            String chainUrl = "http://192.168.2.51:3000/claim";// 本地

            // 发送 http 请求给链上
            HttpResponse response = HttpUtil.createPost(chainUrl)
                    .body(requestBody.toString())// 请求体
                    .timeout(100000)// 设置超时时间（单位：毫秒）
                    .execute();
            System.out.println("response" + response.toString());

            /**
             * 4.根据状态码返回响应信息 msg 和 状态码 statusCode 给前端
             */
            // 打印信息
            statusCode = response.getStatus();
            log.info("statusCode:{}", statusCode);
            responseStr = response.toString();
            log.info("responseStr:{}", responseStr);

            ClaimStatusDto dto = claimStatusJudger(statusCode, responseStr, Long.valueOf(orderIdStr), address);
            log.warn("claim 服务的响应 {}", dto.getMsg());
            if (dto.getStatusCode() == 200) {
                // 更改订单状态
                int update = orderMapper.update(null, Wrappers.lambdaUpdate(OrderDo.class)
                        .eq(OrderDo::getOrderId, Long.valueOf(orderIdStr))
                        .set(OrderDo::getStatus, CHECKED_3  ));// 将分发订单更改为正在确认

                // 成功:将当前用户的累计收益置 0 , 并且将最新领取时间置为今天
                logMapper.update(null, Wrappers.lambdaUpdate(RichRewardLogDo.class)
                        .eq(RichRewardLogDo::getAddress, address)
                        .set(RichRewardLogDo::getTotalReward, BigDecimal.ZERO)// 收益数清零
                        .set(RichRewardLogDo::getLatest, LocalDateTime.now().toLocalDate()));// 最新领取时间更新为今天

                // 领取收益成功 状态码200
                log.info("{}", dto.getMsg());
                return Response.successMsg("claim success");
            } else {
                return Response.successMsg("msg:" +dto.getMsg() + ", code:" + statusCode);
            }

        } catch (HttpException e) {
            // 处理 Http 请求发生异常的情况
            e.printStackTrace();
            log.info("请求链上接 响应结果 http 异常 : {}", e);
            return Response.successMsg("httpException:"+e.getMessage(), "code:"+ statusCode);
        } catch (Exception e) {
            // 处理 Http 请求发生异常的情况
            e.printStackTrace();
            log.info("请求链上接口响应结果异常 : {}", e);
            return Response.successMsg("Exception:"+e.getMessage(), "code:"+ statusCode);
        }

    }




    /**
     * 1.成功 200
     * 2.其他 520
     * 3.未知错误 521
     * @param statusCode
     * @param chaimResponse
     * @param orderId
     * @param address
     * @return
     */
    public ClaimStatusDto claimStatusJudger(Integer statusCode, String chaimResponse, Long orderId , String address) {
        String msg = "chaimResponse：";

        if (statusCode == 200) {
            msg = msg + chaimResponse + ", claim success" + statusCode.toString();
        } else if (statusCode == 410) {
            msg = msg + chaimResponse + ", check not right:" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 400) {
            msg = msg + chaimResponse + ", already collected within 24 hours:" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 406){
            log.info("参数错误");
            msg = msg + chaimResponse + ", param not allow :" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 405){
            log.info("method not allow");
            msg = msg + chaimResponse + ", method not allow :" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 401){
            log.info("sha 错误");
            msg = msg + chaimResponse + ", sha error :" + statusCode.toString();
            statusCode = 520;
        } else if(statusCode == 502){
            msg = msg + chaimResponse + ", claim service is stop :" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 403){
            log.info("ip 不在白名单");
            msg = msg + chaimResponse + ", not in wl :" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 500){
            log.info("链上错误");
            msg = msg + chaimResponse + ", chain error :" + statusCode.toString();
            statusCode = 520;
        } else if (statusCode == 5000){
            log.info("校验");
            msg = msg + chaimResponse + ", check error :" + statusCode.toString();
            statusCode = 520;
        } else {
            log.info("收益领取错误:{}" , msg);
            msg = msg + chaimResponse + ", claim service unknown error :" + statusCode.toString();
            statusCode = 521;
        }

        ClaimStatusDto dto = new ClaimStatusDto();
        dto.setMsg(msg);
        dto.setStatusCode(statusCode);

        return dto;
    }

    @Override
    public Long creatOrder(String address, BigDecimal reward) {

        OrderDo orderDo = new OrderDo();
        Long orderId = IdWorker.getId();
        // 获取最新领取时间
        // 获取当前日期和时间
        LocalDateTime current = LocalDateTime.now();

        orderDo.setOrderId(orderId);
        orderDo.setAddress(address);
        orderDo.setOrderTime(current);
        orderDo.setStatus(GIVING_1);
        orderDo.setRewardAmt(reward);

        int insert = orderMapper.insert(orderDo);

        if (insert == 0) {
            return -1L;
        }

        return orderId;
    }

    /**
     * 此接口只做收益合法性校验
     * @param param
     * @return
     */
    @Override
    public Response claimCheck(ClaimCheckParam param) {

        OrderDo orderDo = orderMapper.selectOneOrder(param.getOrderId());

        System.out.println("Long orderId:" + param.getOrderId().toString());

        if (orderDo == null) {
            return Response.error(CLAIM_NOT_ALLOW_5002, "order not exist");
        }

        BigDecimal rewardAmt = orderDo.getRewardAmt();// 订单中的收益数据

        int orderRes = 0;
        if (orderDo != null && orderDo.getAddress().equals(param.getAddress()) && Objects.equals(new BigDecimal(String.valueOf(param.getReward())), rewardAmt)){
            orderRes = orderMapper.update(null, Wrappers.lambdaUpdate(OrderDo.class)
                    .eq(OrderDo::getOrderId, param.getOrderId())
                    .set(OrderDo::getStatus, CHECKED_3));
            return Response.success("allow");
        } else {
            return Response.error(CLAIM_NOT_ALLOW_5002, "当前收益发放不合法");
        }

    }




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


}
