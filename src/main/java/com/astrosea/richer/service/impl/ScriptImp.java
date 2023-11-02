package com.astrosea.richer.service.impl;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.constant.HttpCode;
import com.astrosea.richer.mapper.OrderMapper;
import com.astrosea.richer.param.ClaimParam;
import com.astrosea.richer.pojo.OrderDo;
import com.astrosea.richer.service.ScriptService;
import com.astrosea.richer.utils.SHA256Util;
import com.astrosea.richer.vo.GiveVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.astrosea.richer.response.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.astrosea.richer.constant.OrderConstant.CHECKING_2;
import static com.astrosea.richer.constant.OrderConstant.GIVING_1;

@Slf4j

@Service
public class ScriptImp implements ScriptService {

    @Autowired
    OrderMapper orderMapper;


    @Override
    public Response claim(ClaimParam param) {


        String address = "DGJsyH8oEr5i3UpgyUHvmn331D8VJ3KCwe";
        log.info("claim address{}", address);
        BigDecimal reward = new BigDecimal("0.1");


        if (true) {

            // 订单 id
            Long orderId = creatOrder(address, reward);

            // 构建收益数据
            GiveVo giveVo = new GiveVo();
            giveVo.setAddress(address);// 收益接收人
            giveVo.setAmount(reward);// 收益数
            giveVo.setOrderId(orderId.toString());

            // 构建盐
            String json = JSONUtil.toJsonStr(giveVo);
            String salt = "CFIrxG7nDq4h2TofxTGlmm220E7UI2JBxf";

            // json 加盐
            String jsonSalt = json + salt;

            // 256 盐
            String sha = SHA256Util.encrypt(jsonSalt);

            // 构造请求体 jsonObj
            JSONObject requestBody = new JSONObject();
            requestBody.set("address", address);
            requestBody.set("amount", reward);
            requestBody.set("orderId", orderId);
            requestBody.set("sha", sha);



            // 构造链上 url
            String chainUrl = "http://61.224.66.40:3000/claim";

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
                            .eq(OrderDo::getOrderId, responseOrderId)
                            .set(OrderDo::getStatus, CHECKING_2));

                    // 返回领取成功的响应
                    return Response.success();

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
                    return Response.error(1000, "claim error" + status);
                }

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

    public Long creatOrder(String address, BigDecimal gains) {

        OrderDo orderDo = new OrderDo();
        Long orderId = IdWorker.getId();
        // 获取最新领取时间
        // 获取当前日期和时间
        LocalDateTime current = LocalDateTime.now();

        orderDo.setOrderId(orderId);
        orderDo.setAddress(address);
        orderDo.setOrderTime(current);
        orderDo.setRewardAmt(gains);
        orderDo.setStatus(GIVING_1);

        int insert = orderMapper.insert(orderDo);

        if (insert == 0) {
            return -1L;
        }

        return orderId;
    }

}

