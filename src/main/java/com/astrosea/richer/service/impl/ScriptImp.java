package com.astrosea.richer.service.impl;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.constant.HttpCode;
import com.astrosea.richer.mapper.AstroResenderMapper;
import com.astrosea.richer.mapper.OrderMapper;
import com.astrosea.richer.mapper.Tax1of1NftDoMapper;
import com.astrosea.richer.mapper.TaxAllNftDoMapper;
import com.astrosea.richer.param.ClaimParam;
import com.astrosea.richer.param.NftResenderParam;
import com.astrosea.richer.pojo.OrderDo;
import com.astrosea.richer.pojo.ResenderOrderDo;
import com.astrosea.richer.pojo.Tax1of1NftDo;
import com.astrosea.richer.pojo.TaxAllNftDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.ScriptService;
import com.astrosea.richer.utils.RarityUtil;
import com.astrosea.richer.utils.SHA256Util;
import com.astrosea.richer.vo.GiveVo;
import com.astrosea.richer.vo.dto.NftResenderDto;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.astrosea.richer.constant.OrderConstant.CHECKING_2;
import static com.astrosea.richer.constant.OrderConstant.GIVING_1;
import static com.astrosea.richer.constant.PayConstant.*;

@Slf4j

@Service
public class ScriptImp implements ScriptService {

    private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);


    @Autowired
    OrderMapper orderMapper;

    @Autowired
    TaxAllNftDoMapper nftMapper;

    @Autowired
    Tax1of1NftDoMapper of1NftDoMapper;

    @Autowired
    AstroResenderMapper resenderMapper;

    @Transactional
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

    @Override
    public Response nftResender(NftResenderParam param) {

        // 获取 nft 信息和创建重发订单
        NftResenderDto dto = getNftResendInfo(param.getNftId(), param.getAddress(),  new BigDecimal(param.getCost()));

        String responseStr = "";
        int statusCode = 0;

        // 构建盐
        String json = JSONUtil.toJsonStr(dto);
        String salt = "CFIrxG7nDq4h2TofxTGlmm220E7UI2JBxf";
        // json 加盐
        String jsonSalt = json + salt;
        // 256 盐
        String sha = SHA256Util.encrypt(jsonSalt);
        // 构造请求体 jsonObj
        JSONObject requestBody = new JSONObject();

        requestBody.set("orderId", dto.getOrderId());
        requestBody.set("from", dto.getFrom());
        requestBody.set("nftUtxo", dto.getNftUtxo());
        requestBody.set("toAddress", dto.getToAddress());
        requestBody.set("sha", sha);

        System.out.println("requestBody:"+requestBody);
        System.out.println("json:" + json);
        System.out.println("orderId:"+ dto.getOrderId());
        System.out.println("from:"+ dto.getFrom());
        System.out.println("nftUtxo:"+ dto.getNftUtxo());
        System.out.println("sha:"+ sha);

        // 构造 http 请求 url
        String chainUrl = "http://94.130.49.158:3000/resendNft";// 线上
//            String chainUrl = "http://192.168.2.51:3000/claim";// 本地

        // 发送 http 请求给链上
        HttpResponse response = HttpUtil.createPost(chainUrl)
                .body(requestBody.toString())// 请求体
                .timeout(100000)// 设置超时时间（单位：毫秒）
                .execute();
        logger.info("补发响应 response:{}", response);

        System.out.println("response" + response.toString());


        /**
         * 根据状态码返回响应信息 msg 和 状态码 statusCode 给前端
         */
        // 打印信息
        statusCode = response.getStatus();
        log.info("statusCode:{}", statusCode);
        responseStr = response.toString();
        String bodyStr = response.body().toString();
        log.info("responseStr:{}", responseStr);

        /**
         * 补发订单回调，失败也别影响正常流程
         */
        resenderMapper.update(null, Wrappers.lambdaUpdate(ResenderOrderDo.class)
                .eq(ResenderOrderDo::getOrderId, dto.getOrderId())
                .set(ResenderOrderDo::getResponseLog, bodyStr)
                .set(ResenderOrderDo::getPayStatus, RESEND_DONE_4));// 存储响应


        return Response.successMsg("responseStr:"+responseStr+"statusCode:"+statusCode);
    }

    public static void main(String[] args) {



        JSONObject requestBody = new JSONObject();

    }

    public NftResenderDto getNftResendInfo(Integer nftId, String address , BigDecimal cost) {

        NftResenderDto dto = new NftResenderDto();

        /***
         * 获取 nft 链上数据
         */
        // 稀有 nft 补发
        if (RarityUtil.isLV1(nftId)) {
            Tax1of1NftDo of1NftDo = of1NftDoMapper.selectOne(Wrappers.lambdaQuery(Tax1of1NftDo.class)
                    .select(Tax1of1NftDo::getNftUtxo, Tax1of1NftDo::getAddress)
                    .eq(Tax1of1NftDo::getNftId, nftId));
            dto.setNftUtxo(of1NftDo.getNftUtxo());
            dto.setToAddress(address);
            dto.setFrom(of1NftDo.getAddress());
        } else {
            // 普通 nft 补发
            TaxAllNftDo nftDo = nftMapper.selectOne(Wrappers.lambdaQuery(TaxAllNftDo.class)
                    .select(TaxAllNftDo::getNftUtxo, TaxAllNftDo::getAddress)
                    .eq(TaxAllNftDo::getNftId, nftId));
            dto.setNftUtxo(nftDo.getNftUtxo());
            dto.setToAddress(address);
            dto.setFrom(nftDo.getAddress());
        }
        /**
         * 创建创建派发订单
         */
        Long order = creatResendOrder(nftId, address, cost, 1, RESENDER_PAY_3, Received_3);

        dto.setOrderId(order.toString());

        return dto;
    }

    /**
     * 创建补发订单
     * @param address
     * @param cost
     * @param mintAmt
     * @param style
     * @param status
     * @return
     */
    @Override
    public Long creatResendOrder(Integer nftId, String address , BigDecimal cost , Integer mintAmt, Integer style, Integer status) {
        ResenderOrderDo orderDo = new ResenderOrderDo();
        Long orderId = IdWorker.getId();// 生成订单id，并返回给前端

        orderDo.setUserUrl(address);// 订单的用户地址
        orderDo.setPayAmount(cost);// 订单的金额
        orderDo.setMintAmount(mintAmt);// 订单 mint 的数量
        orderDo.setStyle(style);// 设置支付身份
        orderDo.setPayStatus(status);// 设置支付状态
        orderDo.setOrderId(orderId);// 设置订单号

        ArrayList<Integer> nftList = new ArrayList<>();
        nftList.add(nftId);
        orderDo.setNftList(nftList.toString());// 设置nftId

        int insert = resenderMapper.insert(orderDo);
        if (insert == 0) {
            log.info("Order creation failed, please try again");
            orderId = -1L;
            return orderId;
        }

        log.info("订单信息 {}", orderDo);
        return orderId;
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

