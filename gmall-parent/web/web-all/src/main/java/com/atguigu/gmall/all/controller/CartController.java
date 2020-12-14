package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @description:
 * @time: 2020/12/14 14:10
 * @author: LIANGBO
 */
@Controller
public class CartController {
    @Autowired
    CartFeignClient cartFeignClient;

    //添加订单
    @GetMapping("addCart.html")
    public String addCart(Long skuId, Long skuNum, CartInfo cartInfo) {
        String userId = "1";
        cartInfo.setUserId(userId);
        cartFeignClient.addCart(cartInfo);
        //重定向到静态页面
        return "redirect:/cart/addCart.html?skuNum=" + cartInfo.getSkuNum();

    }
}
