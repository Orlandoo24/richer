package com.astrosea.richer.controller;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/spider")
public class SpiderController {


    @PostMapping("/rats")
    public String timeTask(HttpServletRequest request) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        System.out.println(timeStamp);

        String url = "https://www.okx.com/priapi/v1/nft/brc/tokens/rats?" + timeStamp + "&token=rats&walletAddress=";

        String res = HttpRequest.get(url)
//                .setHttpProxy("127.0.0.1", 7890)
                .timeout(100000)
                .execute()
                .body();

        System.out.println(res);

        return res;
    }




}
