package com.astrosea.richer.controller;

import com.astrosea.richer.mapper.RewardBaseMapper;
import com.astrosea.richer.param.ClaimCheckParam;
import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.param.GetCoinsParam;
import com.astrosea.richer.param.QueryCoinsParam;
import com.astrosea.richer.pojo.RichRewardBaseDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.vo.BaseRewVo;
import com.astrosea.richer.vo.QueryCoinsVo;
import com.astrosea.richer.vo.UpdateGainsVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/rich")
//@CrossOrigin(origins = "192.168.2.52:5500", methods = {RequestMethod.GET, RequestMethod.POST})
//@CrossOrigin(origins = "http://192.168.2.65:5173/")
public class RichController {

    @Autowired
    RewardBaseMapper rewardBaseMapper;

    @Autowired
    RichService richService;

    @Autowired
    TimeTaskService timeTaskService;

    private static final Logger logger = LoggerFactory.getLogger(RichController.class);


    /**
     * 1.填写当天矿场收益
     * 2.添加收益的同时累加所有当时 nft 持有者的收益
     * @param param
     * @return
     */
    @PostMapping("/updateGains")
    public Response<UpdateGainsVo> updateGains(@RequestBody CreatGainParam param) throws SQLException {
        log.info("填写当天矿场收益入参{}", param);
        Response<UpdateGainsVo> response = timeTaskService.updateGains(param);
        log.info("填写当天矿场收益出参{}", response);
        return response;
    }


    /**
     * 查询当天的矿场产出
     * @param request
     * @return
     */
    @GetMapping("/queryBase")
    public Response<BaseRewVo> baseRew(HttpServletRequest request){

        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 转换为精确到天的日期数据
        LocalDate today = now.toLocalDate();

        RichRewardBaseDo baseDo = rewardBaseMapper.selectOne(Wrappers.lambdaQuery(RichRewardBaseDo.class)
                .select(RichRewardBaseDo::getRewBase)
                .eq(RichRewardBaseDo::getRewData, today));

        if (baseDo == null) {
           return Response.successMsg(null,"Today's earnings have not yet been generated");
        }

        BaseRewVo vo = new BaseRewVo();
        vo.setRewBase(baseDo.getRewBase());

        return Response.success(vo);
    }


    /**
     * 查询当前用户的个人收益
     * @param address
     * @return
     */
    @GetMapping("/queryCoins")
    public Response<QueryCoinsVo> queryCoins(@RequestParam String address) {
        QueryCoinsParam param = new QueryCoinsParam();
        param.setAddress(address);
        log.info("queryCoins入参{}", param);
        // 在接口请求开始处记录时间戳
        long startTime = System.currentTimeMillis();
        Response<QueryCoinsVo> vo = richService.query(param);
        // 在接口请求结束处记录时间戳
        long endTime = System.currentTimeMillis();
        // 计算请求的执行时间
        long executionTime = endTime - startTime;
        // 将执行时间记录到日志中
        logger.info("queryCoins 接口响应速度：{} 毫秒", executionTime);
        log.info("queryCoins出参{}", vo);
        return vo;
    }

    /**
     * 领取个人收益
     * @param address
     * @return
     */
    @GetMapping("/claim")
    public Response claim(@RequestParam String address) {
        // 获取当天的收益
        GetCoinsParam param = new GetCoinsParam();
        param.setAddress(address);
        Response response = richService.claim(param);

        log.info("getCoins{}", address);
        // 在接口请求开始处记录时间戳
        long startTime = System.currentTimeMillis();

        // 在接口请求结束处记录时间戳
        long endTime = System.currentTimeMillis();
        // 计算请求的执行时间
        long executionTime = endTime - startTime;
        // 将执行时间记录到日志中
        logger.info("getCoins 接口响应速度：{} 毫秒", executionTime);
        log.info("getCoins{}", address);

        return response;
    }

    @GetMapping("/claimCheck")
    public Response claimCheck(@RequestParam ClaimCheckParam param) {

        log.info("claimCheck param{}", param);
        // 在接口请求开始处记录时间戳
        long startTime = System.currentTimeMillis();

        Response response = richService.claimCheck(param);

        // 在接口请求结束处记录时间戳
        long endTime = System.currentTimeMillis();
        // 计算请求的执行时间
        long executionTime = endTime - startTime;
        // 将执行时间记录到日志中
        logger.info("claimCheck 接口响应速度：{} 毫秒", executionTime);
        log.info("claimCheck{}", response);

        return response;
    }





}
