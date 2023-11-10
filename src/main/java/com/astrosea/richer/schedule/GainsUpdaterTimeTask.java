package com.astrosea.richer.schedule;

import cn.hutool.json.JSONUtil;
import com.astrosea.richer.mapper.TimeTaskMapper;
import com.astrosea.richer.pojo.TimeTaskLogDo;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.vo.UpdateGainsVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Slf4j
@EnableScheduling
public class GainsUpdaterTimeTask {

    @Autowired
    TimeTaskService timeTaskService;

    @Autowired
    TimeTaskMapper  timeTaskMapper;

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





}
