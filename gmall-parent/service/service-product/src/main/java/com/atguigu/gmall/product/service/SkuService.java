package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;

/**
 * @description:
 * @time: 2020/12/1 15:09
 * @author: LIANGBO
 */
public interface SkuService {
    IPage<SkuInfo> skuList(IPage<SkuInfo> page);

    void saveSkuInfo(SkuInfo skuInfo);

    void onSale(Long skuId);

    void cancelSale(Long skuId);


    BigDecimal getPrice(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);
}
