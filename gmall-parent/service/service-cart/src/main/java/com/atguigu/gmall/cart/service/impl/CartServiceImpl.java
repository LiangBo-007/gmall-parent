package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

/**
 * @description:
 * @time: 2020/12/14 14:45
 * @author: LIANGBO
 */
@Controller
public class CartServiceImpl implements CartService {
    @Autowired
    CartInfoMapper cartInfoMapper;
    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public void addCart(CartInfo cartInfo) {
        //商品id和数量
        Long skuId = cartInfo.getSkuId();
        Integer skuNum = cartInfo.getSkuNum();

        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
        cartInfo.setSkuNum(skuNum);
        cartInfo.setImgUrl(cartInfo.getImgUrl());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setUserId("1");
        cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(skuId)));
        cartInfo.setIsChecked(1);
        cartInfo.setUserId("skuId");
        cartInfoMapper.insert(cartInfo);
    }
}
