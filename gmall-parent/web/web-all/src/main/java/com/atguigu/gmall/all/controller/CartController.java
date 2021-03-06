package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @RequestMapping("addCart.html")
    public String addCart(Long skuId, Long skuNum, CartInfo cartInfo){

        String userId = "1";
        cartInfo.setUserId(userId);

        cartFeignClient.addCart(cartInfo);

        return "redirect:http://cart.gmall.com/cart/addCart.html?skuNum="+cartInfo.getSkuNum();
    }

    @RequestMapping("cart/cart.html")
    public String cartList(){

        String userId = "1";

        return "cart/index";
    }


}
