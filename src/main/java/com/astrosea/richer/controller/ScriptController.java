package com.astrosea.richer.controller;


import com.astrosea.richer.param.ClaimJudgerParam;
import com.astrosea.richer.response.Response;
import com.astrosea.richer.service.ClaimService;
import com.astrosea.richer.service.ScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/script")
public class ScriptController {


    @Autowired
    ScriptService scriptService;

    @Autowired
    ClaimService claimService;

//    /**
//     *
//     * @param param
//     * @return
//     */
//    @PostMapping("/claimScript")
//    public Response claimScript(@RequestBody ClaimScriptParam param) {
//
//        String address = param.getAddress();
//
//        BigDecimal reward = new BigDecimal(param.getReward()).setScale(1, RoundingMode.HALF_UP);
//
//        Long orderId = claimService.creatOrder(address, reward);
//
//        System.out.println(orderId);
//
//        Response response = claimService.chainClaim(address, reward, orderId.toString());
//
//        return response;
//    }

    @PostMapping("/claimJudger")
    public Response claimJudger(@RequestBody ClaimJudgerParam param) {

        String address = param.getAddress();

        Boolean claimJudger = claimService.claimJudger(address);

        if (claimJudger) {
            return Response.success();
        }

        return Response.success(false);
    }








}