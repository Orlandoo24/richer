package com.astrosea.richer.schedule;

import com.astrosea.richer.mapper.TimeTaskMapper;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.utils.MailClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
public class GainsUpdaterTimeTask {

    @Autowired
    TimeTaskService timeTaskService;

    @Autowired
    TimeTaskMapper  timeTaskMapper;

    @Autowired
    MailClient          mailClient;

    @Autowired
    RedisTemplate    redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);






//    /**
//     * 零点的收益定时任务
//     * @throws SQLException
//     */
//    @Scheduled(cron = "0 0 0 * * *") // 此设置服务器在服务器上对应的是北京时间早上 8 点
//    public void timeTaskUpdateGains() throws SQLException {
//        UpdateGainsVo vo = timeTaskService.timeTaskUpdateGains().getData();
//
//        logger.info("timeTaskUpdateGains:{}", vo);
//        TimeTaskLogDo logDo = new TimeTaskLogDo();
//        long orderId = System.currentTimeMillis();
//        String resJsonString = JSONUtil.toJsonStr(vo);
//        logDo.setResLog(resJsonString);
//        logDo.setOrderId(orderId);
//        int insert = timeTaskMapper.insert(logDo);
//
//    }



    @Scheduled(fixedRate = 10000)//
    public void timeTaskRat() {
//        OkxSpider okxSpider = new OkxSpider();
//
//        String usdFloorPrice = getUsdFloorPrice("rats");
//
//        /**
//         * 1.判断当前地板价有无提醒过提醒过则返回 false , 没有则返回 true 、均价 、 编号
//         */
//        UnitUsdFloorPriceAvgDto ratsPriceAvgDto = okxSpider.getUnitUsdFloorPriceAvg("rats");// 获取均价和判断当前地板挂单有无提醒过
//
//        /**
//         * 2.比较地板价和均价的跌幅，超过 50% 则发邮件警告，没有则不发
//         */
//        // rats 邮箱提醒实体
//        PriceDto ratsFloorPriceDto = compareFloorPrice(getUsdFloorPrice("rats"), usdFloorPrice, "rats", ratsPriceAvgDto.getWarn());
//
//        System.out.println(ratsFloorPriceDto);


    }


}
