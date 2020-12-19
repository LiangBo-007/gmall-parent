package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @description:
 * @time: 2020/12/18 19:39
 * @author: LIANGBO
 */
public interface OrderService {

    boolean checkTradeNo(String userId, String tradeNo);

    String submitOrder(OrderInfo order);

    String genTradeNo(String userId);

    OrderInfo getOrderInfoById(Long orderId);
}
