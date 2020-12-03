package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.CategoryService;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    SkuService skuService;

    @Autowired
    SpuService spuService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping("getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId){

        BigDecimal bigDecimal = new BigDecimal("0");

        bigDecimal = skuService.getPrice(skuId);

        return bigDecimal;

    }

    @RequestMapping("getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuService.getSkuInfoById(skuId);

        return skuInfo;
    }


    @RequestMapping("getSpuSaleAttrListBySpuId/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrs = spuService.spuSaleAttrList(spuId);

        return spuSaleAttrs;
    }

    @RequestMapping("getCategoryViewByCategory3Id/{category3Id}")
    BaseCategoryView getCategoryViewByCategory3Id(@PathVariable("category3Id")  Long category3Id){
        BaseCategoryView baseCategoryView = categoryService.getCategoryViewByCategory3Id(category3Id);

        return baseCategoryView;
    }
}
