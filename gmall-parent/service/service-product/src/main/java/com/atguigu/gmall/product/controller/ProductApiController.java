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
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    SkuService skuService;

    @Autowired
    SpuService spuService;

    @Autowired
    CategoryService categoryService;


    @RequestMapping("getSaleAttrValuesBySpu/{spuId}")
    Map<String,Long> getSaleAttrValuesBySpu(@PathVariable("spuId") Long spuId){
        Map<String,Long> map = spuService.getSaleAttrValuesBySpu(spuId);

        return map;
    }

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


    @RequestMapping("getSpuSaleAttrListBySpuId/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@PathVariable("spuId") Long spuId,@PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> spuSaleAttrs = spuService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrs;
    }

    @RequestMapping("getCategoryViewByCategory3Id/{category3Id}")
    BaseCategoryView getCategoryViewByCategory3Id(@PathVariable("category3Id")  Long category3Id){
        BaseCategoryView baseCategoryView = categoryService.getCategoryViewByCategory3Id(category3Id);

        return baseCategoryView;
    }
}
