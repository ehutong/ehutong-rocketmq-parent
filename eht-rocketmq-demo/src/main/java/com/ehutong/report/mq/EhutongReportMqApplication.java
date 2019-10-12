package com.ehutong.report.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
/**
 * 
 * @author Administrator
 * @date 2019/07/23
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableAutoConfiguration
@EnableCaching
@EnableAsync
public class EhutongReportMqApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EhutongReportMqApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // extends SpringBootServletInitializer
        return application.sources(EhutongReportMqApplication.class);
    }

}
