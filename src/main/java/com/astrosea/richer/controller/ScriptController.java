package com.astrosea.richer.controller;


import com.astrosea.richer.param.ClaimJudgerParam;
import com.astrosea.richer.param.ClaimScriptParam;
import com.astrosea.richer.param.CreatGainParam;
import com.astrosea.richer.param.NftResenderParam;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.ClaimService;
import com.astrosea.richer.service.ScriptService;
import com.astrosea.richer.service.TimeTaskService;
import com.astrosea.richer.vo.UpdateGainsVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@RestController
@RequestMapping("/script")
public class ScriptController {

    private static final Logger logger = LoggerFactory.getLogger(ScriptController.class);


    @Autowired
    ScriptService scriptService;

    @Autowired
    ClaimService claimService;

    @Autowired
    TimeTaskService timeTaskService;


    /**
     * 1.填写当天矿场收益
     * 2.添加收益的同时累加所有当时 nft 持有者的收益
     * @param param
     * @return
     */
    @PostMapping("/updateGains")
    public Response<UpdateGainsVo> updateGains(@RequestBody CreatGainParam param) throws SQLException {

        if (param.getFakeRewBase() == null ) {
               return Response.successMsg("参数不能为空");
        }

        if (param.getRealDecBase() == null) {
            return Response.successMsg("参数不能为空");
        }


        log.info("填写当天矿场收益入参{}", param);
        Response<UpdateGainsVo> response = timeTaskService.updateGains(param);
        log.info("填写当天矿场收益出参{}", response);
        return response;
    }



    /**
     *
     * @param param
     * @return
     */
    @PostMapping("/claimScript")
    public Response claimScript(@RequestBody ClaimScriptParam param) {

        String address = param.getAddress();

        BigDecimal reward = new BigDecimal(param.getReward()).setScale(1, RoundingMode.HALF_UP);

        Long orderId = claimService.creatOrder(address, reward);

        System.out.println(orderId);

        Response response = claimService.chainClaim(address, reward, orderId.toString());

        return response;
    }

    @PostMapping("/claimJudger")
    public Response claimJudger(@RequestBody ClaimJudgerParam param) {

        String address = param.getAddress();

        Boolean claimJudger = claimService.claimJudger(address);

        if (claimJudger) {
            return Response.success();
        }

        return Response.success(false);
    }

    /**
     * nft 补发接口
     * @param param
     * @return
     */
    @PostMapping("/nftResender")
    public Response nftResender(@RequestBody NftResenderParam param) {

        // key 为阿花第一次 mint 的 tx_id
        if (!param.getKey().equals("4ac019f9039f42593a9681b159015ee85fc7833990093e43dbcd3170c071a5e0")) {
            return Response.successMsg("not allow");
        }

        logger.info("{}", param);

        Response response = scriptService.nftResender(param);

        return response;
    }

    /**
     * 更新收益脚本接口
     * @param request
     * @return
     * @throws SQLException
     */
    @PostMapping("/timeTaskUpdateGains")
    public Response timeTask(HttpServletRequest request) throws SQLException {

        Response<UpdateGainsVo> response = timeTaskService.timeTaskUpdateGains();

        return response;
    }


    /**
     * 打印时间和时区脚本接口
     * @param request
     * @return
     */
    @PostMapping("/time")
    public Response time(HttpServletRequest request) {
        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println("当前时区：" + zoneId);
        logger.info("{}", zoneId);
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前时间：" + now);
        logger.info("zoneId{}", now);
        return Response.successMsg("当前时区：" + zoneId + "当前时间：" + now);
    }













}
