package com.atguigu.gmall.order.client;

import com.atguigu.gmall.model.order.OrderDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description:
 * @time: 2020/12/18 14:38
 * @author: LIANGBO
 */
@FeignClient("service-order")
public interface OrderFengnClient {
    @RequestMapping("api/order/trade")
    List<OrderDetail> trade();

    @RequestMapping("api/order/genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId);
}


