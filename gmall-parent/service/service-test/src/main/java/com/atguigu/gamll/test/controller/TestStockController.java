package com.atguigu.gamll.test.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @time: 2020/12/6 10:02
 * @author: LIANGBO
 */
@RestController
public class TestStockController {
    @Autowired
    RedisTemplate redisTemplate;
    @RequestMapping("stock01")
    public String stock01(){
        Integer stock = (Integer)redisTemplate.opsForValue().get("stock");
        stock--;
        redisTemplate.opsForValue().set("stock" , stock);
        System.out.println("库存数量："+stock);

        return ""+stock;
    }
}

