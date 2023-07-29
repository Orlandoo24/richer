package com.astrosea.richer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rich")
//@CrossOrigin(origins = "192.168.2.52:5500", methods = {RequestMethod.GET, RequestMethod.POST})
@CrossOrigin(origins = "http://192.168.2.52:5500")
public class RichController {


}
