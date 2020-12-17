package com.atguigu.gmall.cart.controller;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @Autowired
    CartService cartService;

    @RequestMapping("cartList")
    Result cartList(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> cartInfos = cartService.cartList(cartInfo);
        return Result.ok(cartInfos);
    }

    @RequestMapping("addCart")
    void addCart(@RequestBody CartInfo cartInfo, HttpServletRequest request) {

        String userId = request.getHeader("userId");
        cartInfo.setUserId(userId);
        cartService.addCart(cartInfo);
        System.out.println("cartApi");

    }

}
