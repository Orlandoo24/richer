package com.astrosea.richer.schedule;

import cn.hutool.core.util.StrUtil;
import com.astrosea.richer.constant.RedisKeyConstant;
import com.astrosea.richer.utils.BeijingTimeUtil;
import com.astrosea.richer.utils.MailClient;
import com.astrosea.richer.utils.SpiderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@EnableScheduling
public class SpiderTimeTask {

    @Autowired
    MailClient mailClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SpiderClient spiderClient;


    /**
     * 预警邮箱（有去重
     */
//    @Scheduled(fixedRate = 3600000)
//    @Scheduled(fixedRate = 10000)
    public void priceReports()  {

        /**
         * 拉取最新数据
         */

        /**
         * 1.判断当前地板价有无提醒过提醒过则返回 false , 没有则返回 true 、均价 、 编号
         */
        /**
         * 2.比较地板价和均价的跌幅，超过 50% 则发邮件警告，没有则不发
         */
        String mainContent = spiderClient.constructEmail();

        try {

                    String beijingTime = BeijingTimeUtil.getBeijingTime();
                    log.warn("beijingTime:" + beijingTime);
                    String subject = "OKX Ordinals 市场 BRC-20 ：最新地板价跌幅提醒";

                    String content = mailClient.getHtmlContent("astrosea.io@astrosea", mainContent,
                            "Ordinals 市场 BRC-20 价格浮动提醒 / 北京时间 ："+ beijingTime + "\n"
                                    + "(此浮动计算根据当前挂单最低价与价格从低到高排列的后 6 笔均价得出，故皆显示为跌幅)");
            /**
             * 邮件策略
             */
        String aZe = "cryptoyisa@outlook.com";
        String boss =  "wbin7093@gmail.com";
            String me =  "1179530478@qq.com";


            List<String> emails = new ArrayList<>();
//        emails.add(aZe);
//        emails.add(boss);
            emails.add(me);

                    mailClient.sendMail(emails, subject, content);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

    }

    /**
     * 预警邮箱，无去重
     */
//    @Scheduled(fixedRate = 3600000)
//    @Scheduled(fixedRate = 10000) // 10s
    @Scheduled(fixedRate = 300000) // 5 min 扫一次，第一版
    public void easyReports()  {

        /**
         * 拉取最新数据
         */

        /**
         * 1.比较地板价和均价的跌幅，超过 5% 则发邮件警告，没有则不发
         */
        String mainContent = spiderClient.constructEmailEasy();

        if (StrUtil.isNotEmpty(mainContent)) {

            try {

                String beijingTime = BeijingTimeUtil.getBeijingTime();
                log.warn("beijingTime:" + beijingTime);
                String subject = "OKX Ordinals 市场 BRC-20 ：最新地板价跌幅提醒";

                String content = mailClient.getHtmlContent("astrosea.io@astrosea", mainContent,
                        "Ordinals 市场 BRC-20 价格浮动提醒 / 北京时间 ："+ beijingTime + "\n"
                                + "(此浮动计算根据当前挂单最低价与价格从低到高排列的后2～6 笔的均价得出，故皆显示为跌幅)");
                /**
                 * 邮件策略
                 */
                String aZe = "cryptoyisa@outlook.com";
                String boss =  "wbin7093@gmail.com";
                String me =  "1179530478@qq.com";


                List<String> emails = new ArrayList<>();
                emails.add(aZe);
                emails.add(boss);
                emails.add(me);

                mailClient.sendMail(emails, subject + mainContent, content);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }



    }





    /**
     * 如果邮箱不存在则说明从来没发过邮件，setKey 并返回 true，进行发邮件操作
     * 已经过期则说明半小时没发，setKey 并返回 true ，进行发邮件操作
     * 如果存在或未过期则，说明刚刚发过，返回 false
     * @param email
     * @return
     */
    public Boolean warnEmail(String email) {

        // 验证 email 是否过期
        String emailKey = RedisKeyConstant.EMAIL + email;
        String emailValue = (String) redisTemplate.opsForValue().get(emailKey);

        if (StrUtil.isBlank(emailValue)){// 为空，过期或者从未发过邮件
            redisTemplate.opsForValue().set(RedisKeyConstant.EMAIL + email, email, 30, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }






}
