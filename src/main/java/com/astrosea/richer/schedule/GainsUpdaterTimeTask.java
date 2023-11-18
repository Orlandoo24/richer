package com.astrosea.richer.schedule;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.astrosea.richer.mapper.TimeTaskMapper;
import com.astrosea.richer.pojo.TimeTaskLogDo;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.utils.MailClient;
import com.astrosea.richer.vo.UpdateGainsVo;
import com.astrosea.richer.vo.dto.RatsFloorDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@Component
@Slf4j
@EnableScheduling
public class GainsUpdaterTimeTask {

    @Autowired
    TimeTaskService timeTaskService;

    @Autowired
    TimeTaskMapper  timeTaskMapper;

    @Autowired
    MailClient      mailClient;

    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);



//    @Scheduled(cron = "")
    public void test() {
        // 调用接口的代码
        logger.info("定时任务开始执行");
        System.out.println("定时任务开始执行");
        // 处理返回结果的代码
        // ...
        logger.info("定时任务执行完成");
        System.out.println("定时任务执行完成");
    }


    /**
     * 零点的定时任务
     * @throws SQLException
     */
    @Scheduled(cron = "0 0 0 * * *") // 此设置服务器在服务器上对应的是北京时间早上 8 点
    public void timeTaskUpdateGains() throws SQLException {
        UpdateGainsVo vo = timeTaskService.timeTaskUpdateGains().getData();

        logger.info("timeTaskUpdateGains:{}", vo);
        TimeTaskLogDo logDo = new TimeTaskLogDo();
        long orderId = System.currentTimeMillis();
        String resJsonString = JSONUtil.toJsonStr(vo);
        logDo.setResLog(resJsonString);
        logDo.setOrderId(orderId);
        int insert = timeTaskMapper.insert(logDo);

    }

    /**
     *
     *
     */
    @Scheduled(fixedRate = 10000)
    public void rats()  {

        // 0.24 为现在的地板价
//        String nowFloorPrice = getFloor();
        String nowFloorPrice = "0.05";

        // 24h 交易均价为 0.200653568429181912
//        String avgFloorPrice = getAvg();
        String avgFloorPrice = " 0.200653568429181912";

        System.out.println("avgFloorPrice"+avgFloorPrice);

        RatsFloorDto dto = compareFloorPrice(nowFloorPrice, avgFloorPrice);

        System.out.println(dto);

        if (dto.getLowPrice()) {
            sendEmail(dto.getDrop());
        }

    }

    public void sendEmail (String drop) {

        String aZe = "cryptoyisa@outlook.com";

        String to =  "1179530478@qq.com";

        String subject = "Rats 跌价警报";

        try {
            mailClient.sendMail(to, subject, "Rats 的跌价超过了70%， 具体跌幅为：" + drop + "%");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }


    public static void main(String[] args) {

        // 0.24 为现在的地板价
        String nowFloorPrice = "0.05";

        // 24h 交易均价为 0.200653568429181912
        String avgFloorPrice = "0.200653568429181912";

        RatsFloorDto dto = compareFloorPrice(nowFloorPrice, avgFloorPrice);

        System.out.println(dto);
    }


    public static RatsFloorDto compareFloorPrice(String nowFloorPrice, String avgFloorPrice) {
        RatsFloorDto dto = new RatsFloorDto();

        BigDecimal nowPrice = new BigDecimal(nowFloorPrice);
        BigDecimal avgPrice = new BigDecimal(avgFloorPrice.trim());

        BigDecimal decreaseRate = avgPrice.subtract(nowPrice).divide(avgPrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        if (decreaseRate.compareTo(new BigDecimal("70")) >= 0) {
            System.out.println("价格跌幅为：" + decreaseRate);

            dto.setLowPrice(true);
            dto.setDrop(decreaseRate.abs().toString());
        } else {
            dto.setLowPrice(false);
            dto.setDrop(decreaseRate.abs().toString());
        }

        return dto;
    }


    public String getFloor() {
        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println(timeStamp);

        String url = "https://www.okx.com/priapi/v1/nft/brc/tokens/rats?t=" + timeStamp + "&token=rats&walletAddress=";

        String res = HttpRequest.get(url)
//                .setHttpProxy("127.0.0.1", 7890)
                .timeout(100000)
                .execute()
                .body();

        JSONObject json = JSONUtil.parseObj(res);
        JSONObject data = json.getJSONObject("data");
        String floorPrice = data.getStr("floorPrice");

        System.out.println(floorPrice);

        return floorPrice;
    }

    public  String getAvg() {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println(timeStamp);


        String url = "https://www.okx.com/priapi/v1/nft/brc/detail/analytics/trades?t=" + timeStamp + "&ticker=rats&type=1";

        String res = HttpRequest.get(url)
//                .setHttpProxy("127.0.0.1", 7890)
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

        return res;
    }











}
