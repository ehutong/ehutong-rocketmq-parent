package com.ehutong.report.mq.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 达人店铺
 * @author ltp
 * @date 2019/05/14
 */
@Slf4j
@Service
public class MqService {


    @Value("${spring.profiles.active}")
    private String activeProfile;

    public Map<String, Object> demo(Map<String, Object> params){
    	log.info("demo");
    	return new HashMap<String,Object>();
    }

}