package com.astrosea.richer.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.constant.RedisKeyConstant;
import com.astrosea.richer.vo.dto.PriceDto;
import com.astrosea.richer.vo.dto.UnitUsdFloorPriceAvgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Component
public class SpiderClient {

    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 复杂
     * @return
     */
    public String constructEmail() {
        StringBuilder sb = new StringBuilder();

        // 构造rats邮箱提醒实体
        PriceDto ratsWarnDto = compareFloorPrice(getUsdFloorPrice("rats"),
                getUnitUsdFloorPriceAvg("rats").getUnitUsdFloorPriceAvg(), "rats",
                getUnitUsdFloorPriceAvg("rats").getWarn());
        if (ratsWarnDto.getChangeRate() != null) {
            sb.append(ratsWarnDto.getChangeRate()).append("\n");
        }

        // 构造sats邮箱提醒实体
        PriceDto satsWarnDto = compareFloorPrice(getUsdFloorPrice("sats"),
                getUnitUsdFloorPriceAvg("sats").getUnitUsdFloorPriceAvg(), "sats",
                getUnitUsdFloorPriceAvg("sats").getWarn());
        if (satsWarnDto.getChangeRate() != null) {
            sb.append(satsWarnDto.getChangeRate()).append("\n");
        }

        // 构造cats邮箱提醒实体
        PriceDto catsWarnDto = compareFloorPrice(getUsdFloorPrice("cats"),
                getUnitUsdFloorPriceAvg("cats").getUnitUsdFloorPriceAvg(), "cats",
                getUnitUsdFloorPriceAvg("cats").getWarn());
        if (catsWarnDto.getChangeRate() != null) {
            sb.append(catsWarnDto.getChangeRate()).append("\n");
        }

        // 构造csas邮箱提醒实体
        PriceDto csasWarnDto = compareFloorPrice(getUsdFloorPrice("csas"),
                getUnitUsdFloorPriceAvg("csas").getUnitUsdFloorPriceAvg(), "csas",
                getUnitUsdFloorPriceAvg("csas").getWarn());
        if (csasWarnDto.getChangeRate() != null) {
            sb.append(csasWarnDto.getChangeRate()).append("\n");
        }

        // 构造8848邮箱提醒实体
        PriceDto eightEightFourEightWarnDto = compareFloorPrice(getUsdFloorPrice("8848"),
                getUnitUsdFloorPriceAvg("8848").getUnitUsdFloorPriceAvg(), "8848",
                getUnitUsdFloorPriceAvg("8848").getWarn());
        if (eightEightFourEightWarnDto.getChangeRate() != null) {
            sb.append(eightEightFourEightWarnDto.getChangeRate()).append("\n");
        }

        // 构造MMSS邮箱提醒实体
        PriceDto mmssWarnDto = compareFloorPrice(getUsdFloorPrice("MMSS"),
                getUnitUsdFloorPriceAvg("MMSS").getUnitUsdFloorPriceAvg(), "MMSS",
                getUnitUsdFloorPriceAvg("MMSS").getWarn());
        if (mmssWarnDto.getChangeRate() != null) {
            sb.append(mmssWarnDto.getChangeRate());
        }

        return sb.toString();
    }

    /**
     * 不管重复，每次都去比价拿到数据
     * @return
     */
    public String constructEmailEasy() {
        StringBuilder sb = new StringBuilder();

        // 构造rats邮箱提醒实体
        PriceDto ratsWarnDto = compareFloorPrice(getUsdFloorPrice("rats"),
                getSixFloorPriceAvg("rats"), "rats", true);
        if (ratsWarnDto.getChangeRate() != null) {
            sb.append(ratsWarnDto.getChangeRate()).append("\n");
        }

        // 构造sats邮箱提醒实体
        PriceDto satsWarnDto = compareFloorPrice(getUsdFloorPrice("sats"),
                getSixFloorPriceAvg("sats"), "sats", true);
        if (satsWarnDto.getChangeRate() != null) {
            sb.append(satsWarnDto.getChangeRate()).append("\n");
        }

        // 构造cats邮箱提醒实体
        PriceDto catsWarnDto = compareFloorPrice(getUsdFloorPrice("cats"),
                getSixFloorPriceAvg("cats"), "cats", true);
        if (catsWarnDto.getChangeRate() != null) {
            sb.append(catsWarnDto.getChangeRate()).append("\n");
        }

        // 构造csas邮箱提醒实体
        PriceDto csasWarnDto = compareFloorPrice(getUsdFloorPrice("csas"),
                getSixFloorPriceAvg("csas"), "csas", true);
        if (csasWarnDto.getChangeRate() != null) {
            sb.append(csasWarnDto.getChangeRate()).append("\n");
        }

        // 构造8848邮箱提醒实体
        PriceDto eightEightFourEightWarnDto = compareFloorPrice(getUsdFloorPrice("8848"),
                getSixFloorPriceAvg("8848"), "8848", true);
        if (eightEightFourEightWarnDto.getChangeRate() != null) {
            sb.append(eightEightFourEightWarnDto.getChangeRate()).append("\n");
        }

        // 构造MMSS邮箱提醒实体
        PriceDto mmssWarnDto = compareFloorPrice(getUsdFloorPrice("MMSS"),
                getSixFloorPriceAvg("MMSS"), "MMSS", true);
        if (mmssWarnDto.getChangeRate() != null) {
            sb.append(mmssWarnDto.getChangeRate());
        }

        return sb.toString();
    }




    /**
     * 比价接口
     * 跌幅超 50% 达到报警阈值
     *
     * @param nowFloorPrice
     * @param avgPrice
     * @param brc
     * @param
     * @return
     */
    public static PriceDto compareFloorPrice(String nowFloorPrice, String avgPrice, String brc , Boolean warn) {

        PriceDto dto = new PriceDto();
        dto.setChangeRate(null);// 邮箱浮动提醒初始值为空
        dto.setLowPriceWarn(false);// 初始提醒为否

        // 若不需要提醒直接返回 dto
        if (!warn) {
            return dto;
        }

        BigDecimal floor = new BigDecimal(nowFloorPrice);
        BigDecimal nowAvg = new BigDecimal(avgPrice.trim());


        BigDecimal change = floor.subtract(nowAvg);
        System.out.println("change :" + change);

        BigDecimal changeRate = change.divide(nowAvg, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));// 涨幅百分数
        System.out.println("changeRate :" + changeRate);


        if (changeRate.compareTo(BigDecimal.ZERO) > 0) {
//            dto.setChangeRate(changeRate + "%");
//            dto.setLowPriceWarn(true);// 只是上涨

            System.out.println("涨幅百分比为：" + changeRate + "%");

        } else if (changeRate.compareTo(BigDecimal.ZERO) < 0) {
//            dto.setChangeRate(changeRate + "%");
//            dto.setLowPriceWarn(true);// 只是下跌

            System.out.println("跌幅百分比为：" + changeRate + "%");

            if (changeRate.compareTo(new BigDecimal("-50")) <= 0) {// 测试阶段为-5%就预警，正式为-50%
                dto.setChangeRate(brc + "具体浮动为：" + changeRate + "%");
                dto.setLowPriceWarn(true);// 达到报警阈值

                System.out.println("达到报警阈值：" + changeRate + "%");
            }

        } else {
            System.out.println(brc + "无变化");
        }

        return dto;
    }




    /**
     * 获取欧易实时地板价 USD
     * @return
     */
    public String getUsdFloorPrice(String brc) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println("getUsdFloorPrice timeStamp :" +timeStamp);

        String url = "https://www.okx.com/priapi/v1/nft/brc/tokens/"+ brc
                +"?t=" + timeStamp
                + "&token="
                + brc +"&walletAddress=";

        String res = HttpRequest.get(url)
//                .setHttpProxy("127.0.0.1", 7890)
                .timeout(100000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(res);
        JSONObject data = json.getJSONObject("data");
        String usdFloorPrice = data.getStr("usdFloorPrice");

        System.out.println("usdFloorPrice:" + usdFloorPrice);

        return usdFloorPrice;
    }


    /**
     * 获取后六笔挂单的均价
     * @param brc 币种
     * @return 后六笔挂单的均价
     */
    public String getSixFloorPriceAvg(String brc) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String url = "https://www.okx.com/priapi/v1/nft/brc/detail/items?t="
                + timeStamp
                + "&pageNum=1&pageSize=7&cursor=&ticker="
                + brc + "&orderType=1";

        String res = HttpRequest.get(url)
//                .setHttpProxy("127.0.0.1", 7890)
                .timeout(10000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(res);
        JSONObject data = json.getJSONObject("data");
        JSONArray items = data.getJSONArray("items");

        double totalUsdPrice = 0;
        int count = 0;
        for (int i = 1; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject unitPrice = item.getJSONObject("unitPrice");
            double usdPrice = unitPrice.getDouble("usdPrice");
            totalUsdPrice += usdPrice;
            count++;
        }

        double averageUsdPrice = totalUsdPrice / count;
        return String.format("%.8f", averageUsdPrice);
    }



    /**
     * 获得实时平均单价（最低价后六笔的均价，有做去重逻辑
     * @param brc
     * @return
     */
    public UnitUsdFloorPriceAvgDto getUnitUsdFloorPriceAvg(String brc) {

        UnitUsdFloorPriceAvgDto dto = new UnitUsdFloorPriceAvgDto();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String url = "https://www.okx.com/priapi/v1/nft/brc/detail/items?t="
                + timeStamp
                + "&pageNum=1&pageSize=7&cursor=&ticker="
                + brc + "&orderType=1";

        String res = HttpRequest.get(url)
//                .setHttpProxy("127.0.0.1", 7890)
                .timeout(10000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(res);
        JSONObject data = json.getJSONObject("data");
        JSONArray items = data.getJSONArray("items");

        double totalUsdPrice = 0;
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            if (i == 0) {
                String inscriptionNum = item.getStr("inscriptionNum");
                String tokenId = item.getStr("tokenId");

                ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
                // 验证 email 是否过期
                String tokenIdValue = (String) opsForValue.get(RedisKeyConstant.BRC_TOKEN_ID + tokenId);

                // 验证订单还在不在
                if (StrUtil.isBlank(tokenIdValue)){
                    // 不存在则 , 说明是新低价订单需要警告 ， 存到 redis 中
                    opsForValue.set(RedisKeyConstant.BRC_TOKEN_ID + tokenId, tokenId, 30, TimeUnit.MINUTES);
                    dto.setWarn(true); // 需要发邮件
                    dto.setInscriptionNum(inscriptionNum);// 返回挂单 id
                } else {
                    // 存在则已经警告过
                    dto.setWarn(false);
                    return dto;
                }

            } else {
                JSONObject unitPrice = item.getJSONObject("unitPrice");
                double usdPrice = unitPrice.getDouble("usdPrice");
                totalUsdPrice += usdPrice;
                count++;
            }

        }

        double averageUsdPrice = totalUsdPrice / count;
        dto.setUnitUsdFloorPriceAvg(String.format("%.8f", averageUsdPrice));

        return dto;
    }





    /**
     * 获取欧易实时地板价 sats
     * @return
     */
    public static String getFloorSat(String brc) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println("getFloor timeStamp :" +timeStamp);

        String url = "https://www.okx.com/priapi/v1/nft/brc/tokens/"+ brc
                +"?t=" + timeStamp
                + "&token="
                + brc +"&walletAddress=";

        String res = HttpRequest.get(url)
                .setHttpProxy("127.0.0.1", 7890)
                .timeout(100000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(res);
        JSONObject data = json.getJSONObject("data");
        String floorPrice = data.getStr("floorPrice");

        System.out.println("floorPrice:" + floorPrice);

        return floorPrice;
    }




    /**
     * 获取欧易前7笔
     * @return
     */
    public static String getListAvg(String brc) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println("getFloor timeStamp :" +timeStamp);

        String url = "https://www.okx.com/priapi/v1/nft/brc/detail/items/"+ brc
                +"?t=" + timeStamp
                + "&token="
                + brc +"&walletAddress=";

        String res = HttpRequest.get(url)
                .setHttpProxy("127.0.0.1", 7890)
                .timeout(100000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(res);
        JSONObject data = json.getJSONObject("data");
        String floorPrice = data.getStr("floorPrice");

        System.out.println("floorPrice:" + floorPrice);

        return floorPrice;
    }

    /**
     * 获取欧易实时24h交易均价
     * @return
     */
    public static String get24hAvg(String brc) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println("get24hAvg timeStamp :" + timeStamp);


        String url = "https://www.okx.com/priapi/v1/nft/brc/detail/analytics/trades?t=" + timeStamp
                + "&ticker="
                + brc + "&type=1";

        String res = HttpRequest.get(url)
                .setHttpProxy("127.0.0.1", 7890)
                .timeout(100000)
                .execute()
                .body();

        // 解析JSON数据
        JSONObject json = JSONUtil.parseObj(res);
        JSONObject avgPriceObj = json.getJSONObject("data").getJSONObject("avgPrice");
        String value = avgPriceObj.getStr("value");

        // 将字符串转换为BigDecimal
        BigDecimal avgPriceValue = new BigDecimal(value);

        System.out.println(value);

        return value;
    }

}
