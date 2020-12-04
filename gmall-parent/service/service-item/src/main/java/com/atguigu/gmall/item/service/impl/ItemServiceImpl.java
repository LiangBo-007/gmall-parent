package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getItem(Long skuId) {

        Map<String, Object> mapResult = new HashMap<>();

        BigDecimal bigDecimal = productFeignClient.getPrice(skuId);

        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);

        List<SpuSaleAttr> spuSaleAttrs =  productFeignClient.getSpuSaleAttrListBySpuId(skuInfo.getSpuId(),skuId);

        BaseCategoryView baseCategoryView = productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());

        // 根据spuId查询出来的sku和销售属性值id的对应关系hash表
        Map<String,Long> jsonMap = productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());

        mapResult.put("price",bigDecimal);
        mapResult.put("skuInfo",skuInfo);
        mapResult.put("spuSaleAttrList",spuSaleAttrs);
        mapResult.put("categoryView",baseCategoryView);
        String json = JSON.toJSONString(jsonMap);
        System.out.println(json);
        mapResult.put("valuesSkuJson", json);
        return mapResult;
    }
}
