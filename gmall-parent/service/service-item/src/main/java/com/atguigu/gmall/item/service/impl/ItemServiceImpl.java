package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    ListFeignClient listFeignClient;

    //调用多线程查询
    @Override
    public Map<String, Object> getItem(Long skuId) {
        long currentTimeMillisStart = System.currentTimeMillis();
        Map<String, Object> mapResult = getItemByThread(skuId);
        long currentTimeMillisEnd = System.currentTimeMillis();
        // 为当前skuId的搜索引擎增加热度值
        listFeignClient.hotScore(skuId);
        System.out.println("执行时间：" + (currentTimeMillisEnd - currentTimeMillisStart));
        return mapResult;
    }

    //根据skuId查询商品库存信息
    private Map<String, Object> getItemByThread(Long skuId) {
        Map<String, Object> mapResult = new HashMap<>();

        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
                mapResult.put("skuInfo", skuInfo);
                return skuInfo;
            }
        }, threadPoolExecutor);
        //异步查询价格
        CompletableFuture<Void> completableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal bigDecimal = productFeignClient.getPrice(skuId);
                mapResult.put("price", bigDecimal);
            }
        }, threadPoolExecutor);
        //查询商品销售属性值
        CompletableFuture<Void> completableFutureSaleAttrs = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getSpuSaleAttrListBySpuId(skuInfo.getSpuId(), skuId);
                mapResult.put("spuSaleAttrList", spuSaleAttrs);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> completableFutureCategory = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());
                mapResult.put("categoryView", baseCategoryView);
            }
        }, threadPoolExecutor);
        CompletableFuture<Void> completableFutureJsonMap = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Map<String, Long> jsonMap = productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());
                String json = JSON.toJSONString(jsonMap);
                System.out.println(json);
                mapResult.put("valuesSkuJson", json);
            }
        }, threadPoolExecutor);

        CompletableFuture.allOf(completableFutureSkuInfo, completableFuturePrice, completableFutureSaleAttrs, completableFutureCategory, completableFutureJsonMap).join();

        return mapResult;
    }


    /**
     * @description:线程串行
     * @return:
     * @time: 2020/12/7 14:21
     * @author: LIANG BO
     */


    public Map<String, Object> getItemBySingle(Long skuId) {

        Map<String, Object> mapResult = new HashMap<>();

        BigDecimal bigDecimal = productFeignClient.getPrice(skuId);

        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);

        List<SpuSaleAttr> spuSaleAttrs = productFeignClient.getSpuSaleAttrListBySpuId(skuInfo.getSpuId(), skuId);

        BaseCategoryView baseCategoryView = productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());

        // 根据spuId查询出来的sku和销售属性值id的对应关系hash表
        Map<String, Long> jsonMap = productFeignClient.getSaleAttrValuesBySpu(skuInfo.getSpuId());

        mapResult.put("price", bigDecimal);
        mapResult.put("skuInfo", skuInfo);
        mapResult.put("spuSaleAttrList", spuSaleAttrs);
        mapResult.put("categoryView", baseCategoryView);
        String json = JSON.toJSONString(jsonMap);
        mapResult.put("valuesSkuJson", json);
        return mapResult;
    }
}
