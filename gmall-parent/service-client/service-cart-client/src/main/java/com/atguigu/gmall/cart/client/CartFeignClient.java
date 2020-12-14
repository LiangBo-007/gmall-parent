package com.atguigu.gmall.cart.client;

import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @time: 2020/12/14 14:18
 * @author: LIANGBO
 */
@FeignClient(value = "service-cart")
public interface CartFeignClient {
    @RequestMapping("api/cart/addCart")
    void addCart(CartInfo cartInfo);
}
