package com.atguigu.gmall.cart.controller;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/cart")
@CrossOrigin
public class CartApiController {

    @Autowired
    CartService cartService;

    @RequestMapping("cartList")
    Result cartList(){

        String userId = "1";

        CartInfo cartInfo = new CartInfo();

        cartInfo.setUserId(userId);


        List<CartInfo> cartInfos = cartService.cartList(cartInfo);

        return Result.ok(cartInfos);
    }

    @RequestMapping("addCart")
    void addCart(@RequestBody CartInfo cartInfo){

        String userId = "1";

        cartInfo.setUserId(userId);

        cartService.addCart(cartInfo);

        System.out.println("cartApi");

    }

}
