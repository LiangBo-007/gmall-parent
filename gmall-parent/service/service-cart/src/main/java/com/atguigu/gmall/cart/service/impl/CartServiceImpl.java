package com.atguigu.gmall.cart.service.impl;


import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void addCart(CartInfo cartInfo) {
        Long skuId = cartInfo.getSkuId();
        Integer skuNum = cartInfo.getSkuNum();
        String userId = cartInfo.getUserId();

        // 判断购物车中是否已经添加过
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sku_id",skuId);
        CartInfo cartInfoFromDb = cartInfoMapper.selectOne(queryWrapper);

        if(null==cartInfoFromDb){
            SkuInfo skuInfoById = productFeignClient.getSkuInfoById(skuId);// 调用product模块查询sku详情
            cartInfo.setCartPrice(skuInfoById.getPrice().multiply(new BigDecimal(skuNum)));
            cartInfo.setSkuNum(skuNum);
            cartInfo.setSkuName(skuInfoById.getSkuName());
            cartInfo.setImgUrl(skuInfoById.getSkuDefaultImg());
            cartInfo.setUserId(userId);
            cartInfo.setIsChecked(1);
            cartInfo.setSkuId(skuId);
            // 购物车保存时，没有skuPrice字段，因为一致性差，skuproce字段只能从sku表中查询
            cartInfoMapper.insert(cartInfo);
        }else{
            cartInfo = cartInfoFromDb;// 如果已经添加过，后面要将db的cartInfo覆盖掉缓存
            cartInfo.setSkuNum(cartInfoFromDb.getSkuNum()+skuNum);
            cartInfoMapper.update(cartInfo,queryWrapper);

        }

        // 同步缓存
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId() + RedisConst.USER_CART_KEY_SUFFIX, cartInfo.getSkuId() + "", cartInfo);


    }

    // @GmallCache
    @Override
    public List<CartInfo> cartList(CartInfo cartInfo) {

        // 先取缓存数据
        List<CartInfo> cartInfos = (List<CartInfo>) redisTemplate.opsForHash().values("user:" + cartInfo.getUserId() + ":cart");

        if (null == cartInfos || cartInfos.size() <= 0) {
            Map<String, Object> cacheMap = new HashMap();
            // 查询数据库
            QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
            cartInfoQueryWrapper.eq("user_id", cartInfo.getUserId());
            cartInfos = cartInfoMapper.selectList(cartInfoQueryWrapper);
            if (null != cartInfos && cartInfos.size() > 0) {
                for (CartInfo info : cartInfos) {
                    cacheMap.put(info.getSkuId() + "", info);
                }
                // 同步缓存
                redisTemplate.opsForHash().putAll("user:" + cartInfo.getUserId() + ":cart", cacheMap);
            }

        }

        // 只有在页面展示时才放入skuPrice
        if (null != cartInfos && cartInfos.size() > 0) {
            for (CartInfo info : cartInfos) {
                SkuInfo skuInfoById = productFeignClient.getSkuInfoById(info.getSkuId());
                info.setSkuPrice(skuInfoById.getPrice());
            }
        }


        return cartInfos;
    }
}
