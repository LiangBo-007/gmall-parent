package com.atguigu.gmall.mq.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.atguigu.gmall.mq.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestMqProducer {

    @Autowired
    RabbitService rabbitService;

    @RequestMapping("api/mq/testSendMessage/{message}")
    public Result testSendMessage(@PathVariable("message") String message){
        // 发送消息
        rabbitService.sendMessage("confirm.exchange","confirm.routing",message);
        return Result.ok();
    }

    @RequestMapping("api/mq/testSendDeadMessage/{message}")
    public Result testSendDeadMessage(@PathVariable("message") String messageStr){
        // 发送死信消息
        rabbitService.sendDeadMessage(DeadLetterMqConfig.exchange_dead,DeadLetterMqConfig.routing_1,messageStr,15l, TimeUnit.SECONDS);
        return Result.ok();
    }

    @RequestMapping("api/mq/testSendDelayMessage/{message}")
    public Result testSendDelayMessage(@PathVariable("message") String messageStr){
        // 发送死信消息
        rabbitService.sendDelayMessage("exchange.delay","routing.delay",messageStr,10l, TimeUnit.SECONDS);
        return Result.ok();
    }


}
