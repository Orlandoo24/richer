package com.astrosea.richer.controller;


import com.astrosea.richer.param.ClaimJudgerParam;
import com.astrosea.richer.param.ClaimScriptParam;
import com.astrosea.richer.param.NftResenderParam;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.ClaimService;
import com.astrosea.richer.service.ScriptService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@RestController
@RequestMapping("/script")
public class ScriptController {

    private static final Logger logger = LoggerFactory.getLogger(ScriptController.class);


    @Autowired
    ScriptService scriptService;

    @Autowired
    ClaimService claimService;

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










}
