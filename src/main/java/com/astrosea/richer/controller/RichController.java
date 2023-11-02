package com.astrosea.richer.controller;

import com.astrosea.richer.mapper.RewardBaseMapper;
import com.astrosea.richer.param.ClaimCheckParam;
import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.param.QueryCoinsParam;
import com.astrosea.richer.pojo.RichBaseDo;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.ClaimService;
import com.astrosea.richer.service.RichService;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.vo.BaseRewVo;
import com.astrosea.richer.vo.QueryCoinsVo;
import com.astrosea.richer.vo.UpdateGainsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    ClaimService claimService;

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
        BaseRewVo vo = new BaseRewVo();

        // 获取当前日期和时间
        LocalDateTime now = LocalDateTime.now();
        // 转换为精确到天的日期数据
        LocalDate today = now.toLocalDate();


        QueryWrapper<RichBaseDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("rew_base")
                .eq("is_deleted", false)
                .orderByDesc("rew_data")
                .last("LIMIT 1");

        RichBaseDo baseDo = rewardBaseMapper.selectOne(queryWrapper);

        if (baseDo == null) {
            vo.setRewBase(1888L);
           return Response.successMsg(vo,"OMG!");
        }


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

        Response response = claimService.claim(address);

        return response;
    }

    /**
     * 此接口只做收益校验
     * @param
     * @return
     */
    @PostMapping("/claimCheck")
    public Response claimCheck(@RequestBody ClaimCheckParam param) {

        log.info("claimCheck param{}", param);
        log.info("claimCheck order{}", param.getOrderId());


        Response response = claimService.claimCheck(param);

        return response;
    }





}
