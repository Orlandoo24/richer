package com.astrosea.richer.controller;

import com.astrosea.richer.schedule.GainsUpdaterTimeTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/spider")
public class SpiderController {

    @Autowired
    GainsUpdaterTimeTask timeTask;




    @PostMapping("/email")
    public String email(HttpServletRequest request) {




        return "floor:";
    }




}
