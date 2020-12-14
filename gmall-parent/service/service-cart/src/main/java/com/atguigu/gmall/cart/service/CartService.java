package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @time: 2020/12/14 14:43
 * @author: LIANGBO
 */
@Service
public interface CartService {

    void addCart(CartInfo cartInfo);
}
