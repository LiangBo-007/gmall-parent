package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    OrderService orderService;


    @RequestMapping("trade")
    public List<OrderDetail> trade(HttpServletRequest request, Model model) {

        String userId = request.getHeader("userId");

        List<OrderDetail> orderDetails = new ArrayList<>();

        List<CartInfo> cartInfos = cartFeignClient.cartList(userId);
        if (null != cartInfos && cartInfos.size() > 0) {
            for (CartInfo cartInfo : cartInfos) {
                if (cartInfo.getIsChecked() == 1) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setSkuId(cartInfo.getSkuId());
                    orderDetail.setSkuName(cartInfo.getSkuName());
                    orderDetail.setImgUrl(cartInfo.getImgUrl());
                    orderDetail.setOrderPrice(cartInfo.getCartPrice());
                    orderDetail.setSkuNum(cartInfo.getSkuNum());
                    orderDetails.add(orderDetail);
                }
            }
        }

        return orderDetails;
    }

    //提交订单
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo order, HttpServletRequest request, String tradeNo, Model model) {

        String userId = request.getHeader("userId");
        // 验证tradeNo
        boolean b = orderService.checkTradeNo(userId, tradeNo);
        if (b) {
            order.setUserId(Long.parseLong(userId));
            String orderId = orderService.submitOrder(order);
            return Result.ok(orderId);
        } else {
            return Result.fail();
        }

    }

    //保存下单信息
    @RequestMapping("genTradeNo/{userId}")
    String genTradeNo(@PathVariable("userId") String userId) {
        String TradeNo = orderService.genTradeNo(userId);
        return TradeNo;
    }


    //获取订单信息
    @RequestMapping("getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable("orderId") Long orderId) {
        OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
        return orderInfo;
    }
}
