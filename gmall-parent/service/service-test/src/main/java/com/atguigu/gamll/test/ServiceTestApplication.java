package com.atguigu.gamll.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @description:
 * @time: 2020/12/6 10:05
 * @author: LIANGBO
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTestApplication.class, args);
    }
}
