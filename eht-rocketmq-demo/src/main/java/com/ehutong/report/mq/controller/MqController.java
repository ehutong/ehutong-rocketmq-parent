package com.ehutong.report.mq.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ehutong.report.mq.service.MqService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * ExcelController
 */
@Slf4j
@RestController
@RequestMapping("daren")
public class MqController {

    @Resource
    private MqService service;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @RequestMapping(value = "/action", method = RequestMethod.GET)
    public Map<String,Object> action(@RequestParam Map<String, Object> params) {
        log.info(">>>>>{}.{}: params:{}", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Gson().toJson(params));
        return service.demo(params);
    }


}
