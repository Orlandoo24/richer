package com.astrosea.richer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.mapper.RewardBaseMapper;
import com.astrosea.richer.mapper.RichHolderMapper;
import com.astrosea.richer.mapper.RichRewardLogMapper;
import com.astrosea.richer.mapper.TaxAllNftDoMapper;
import com.astrosea.richer.param.GetCoinsParam;
import com.astrosea.richer.pojo.RichHolderDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.utils.SHA256Util;
import com.astrosea.richer.vo.GiveVo;
import com.astrosea.richer.vo.dto.HolderDto;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@Slf4j

@SpringBootTest
class RicherApplicationTests {

    @Autowired
    RewardBaseMapper rewardBaseMapper;

    @Autowired
    RichService richService;

    @Autowired
    RichRewardLogMapper logMapper;

    @Autowired
    TaxAllNftDoMapper nftMapper;




    @Test
    void contextLoads() {

        List<HolderDto> nftCountByRarity = nftMapper.getNftCountByRarity();

        System.out.println(nftCountByRarity);

    }

    @Test
    void fillTest() {



    }


    @Test
    void fillTest1() {

        GetCoinsParam param = new GetCoinsParam();
        param.setAddress("DGJsyH8oEr5i3UpgyUHvmn331D8VJ3KCwe");

        Response response = richService.claim(param);

    }


    @Test
    void http() {

        BigDecimal reward = new BigDecimal("0.1");

        // 构建收益数据
        GiveVo giveVo = new GiveVo();
        giveVo.setAddress("DGJsyH8oEr5i3UpgyUHvmn331D8VJ3KCwe");
        giveVo.setAmount(reward);


        // 构建盐
        String json = JSONUtil.toJsonStr(giveVo);

        System.out.println("json:"+json);

        String salt = "CFIrxG7nDq4h2TofxTGlmm220E7UI2JBxf";

        // json 加盐
        String jsonSalt = json + salt;

        // 256 盐
        String sha = SHA256Util.encrypt(jsonSalt);


        // 构造请求体 jsonObj
        JSONObject requestBody = new JSONObject();
        requestBody.set("address", "DGJsyH8oEr5i3UpgyUHvmn331D8VJ3KCwe");
        requestBody.set("amount", reward);
        requestBody.set("sha", sha);

        System.out.println("requestBody:"+requestBody);
        System.out.println("amount："+reward);
        System.out.println("sha："+sha);

        // 构造链上 url
        String chainUrl = "http://154.221.27.158:3000/claim";

        try {
//              发送 http 请求给链上
            HttpResponse response = HttpUtil.createPost(chainUrl)
                    .body(requestBody.toString())// 请求体
                    .timeout(30000)// 设置超时时间（单位：毫秒）
                    .execute();
            // 响应 string
            String responseStr = response.body();

            System.out.println(responseStr);

        } catch (Exception e) {

            // 处理 Http 请求发生异常的情况
            e.printStackTrace();
            log.info("请求链上接口响应结果异常 : {}", e);

        }

    }



    public static void main(String[] args) {
        BigDecimal reward = new BigDecimal("0.1");

        // 构建收益数据
        GiveVo giveVo = new GiveVo();
        giveVo.setAddress("DGJsyH8oEr5i3UpgyUHvmn331D8VJ3KCwe");
        giveVo.setAmount(reward);


        // 构建盐
        String json = JSONUtil.toJsonStr(giveVo);

        System.out.println("json:"+json);

        String salt = "CFIrxG7nDq4h2TofxTGlmm220E7UI2JBxf";

        // json 加盐
        String jsonSalt = json + salt;

        // 256 盐
        String sha = SHA256Util.encrypt(jsonSalt);


        // 构造请求体 jsonObj
        JSONObject requestBody = new JSONObject();
        requestBody.set("address", "DGJsyH8oEr5i3UpgyUHvmn331D8VJ3KCwe");
        requestBody.set("amount", reward);
        requestBody.set("sha", sha);

        System.out.println("requestBody:"+requestBody);
        System.out.println("amount"+reward);
        System.out.println("sha"+sha);

        // 构造链上 url
        String chainUrl = "http://154.221.27.158:3000/claim";

        try {
//              发送 http 请求给链上
            HttpResponse response = HttpUtil.createPost(chainUrl)
                    .body(requestBody.toString())// 请求体
                    .timeout(30000)// 设置超时时间（单位：毫秒）
                    .execute();
            // 响应 string
            String responseStr = response.body();

            System.out.println(responseStr);

        } catch (Exception e) {

            // 处理 Http 请求发生异常的情况
            e.printStackTrace();
            log.info("请求链上接口响应结果异常 : {}", e);

        }
    }


    @Autowired
    RichHolderMapper holderMapper;

    @Test
    void Rarity() {
        List<HolderDto> HolderList = nftMapper.getNftCountByRarity();

        for (HolderDto nftDto : HolderList) {

            RichHolderDo holderDo = holderMapper.selectOne(Wrappers.lambdaQuery(RichHolderDo.class)
                    .eq(RichHolderDo::getAddress, nftDto.getAddress()));

            int res = 0;
            if (holderDo == null) {
                holderDo = new RichHolderDo();
                BeanUtil.copyProperties(nftDto, holderDo);
                res = holderMapper.insert(holderDo);
            } else {
                res = holderMapper.update(holderDo, Wrappers.lambdaUpdate(RichHolderDo.class)
                        .eq(RichHolderDo::getAddress, nftDto.getAddress()));
            }

        }


        System.out.println(HolderList);
    }

}
