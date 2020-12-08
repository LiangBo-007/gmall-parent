package com.atguigu.gmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:线程池
 * @time: 2020/12/7 11:33
 * @author: LIANGBO
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor ThreadPoolConfig() {
        return new ThreadPoolExecutor(100, 1000, 60,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(20000));
    }
}
