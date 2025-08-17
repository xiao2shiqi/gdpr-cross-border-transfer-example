package com.hotelbooking.china_bi_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 中国BI系统主应用类
 * 负责展示全球酒店运营数据
 */
@SpringBootApplication
@MapperScan("com.hotelbooking.china_bi_system.mapper")
public class ChinaBiSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinaBiSystemApplication.class, args);
    }
}
