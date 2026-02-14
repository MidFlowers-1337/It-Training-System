package com.itts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * IT技能培训智能选课系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.itts.modules.*.mapper")
@EnableAsync
public class ItTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItTrainingApplication.class, args);
    }
}
