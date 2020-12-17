package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    TrademarkService trademarkService;
/**
 *  @description:
 * @return: es根据skuid获取搜索信息
 * @time: 2020/12/9 23:06
 * @author: LIANG BO
 */    
    @RequestMapping("getSearchAttrList/{skuId}")
    List<SearchAttr> getSearchAttrList(@PathVariable("skuId") Long skuId){

        List<SearchAttr> searchAttrs = baseAttrInfoService.getSearchAttrList(skuId);

        return searchAttrs;
    }
/**
 *  @description:
 * @return: es尚品品牌信息
 * @time: 2020/12/9 23:06
 * @author: LIANG BO
 */    
    @RequestMapping("getTrademarkById/{tmId}")
    BaseTrademark getTrademarkById(@PathVariable("tmId") Long tmId){
        BaseTrademark baseTrademark =  trademarkService.getTrademarkById(tmId);
        return baseTrademark;
    }

    @RequestMapping("getBaseCategoryList")
    List<JSONObject> getBaseCategoryList(){

        List<JSONObject> list = categoryService.getBaseCategoryList();

        return list;
    }



    @RequestMapping("getSaleAttrValuesBySpu/{spuId}")
    Map<String, Long> getSaleAttrValuesBySpu(@PathVariable("spuId") Long spuId) {
        Map<String, Long> map = spuService.getSaleAttrValuesBySpu(spuId);

        return map;
    }

    @RequestMapping("getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId) {

        BigDecimal bigDecimal = new BigDecimal("0");

        bigDecimal = skuService.getPrice(skuId);

        return bigDecimal;

    }

    @RequestMapping("getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userId = request.getHeader("userId");
        SkuInfo skuInfo = skuService.getSkuInfoById(skuId);

        return skuInfo;
    }


    @RequestMapping("getSpuSaleAttrListBySpuId/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId) {
        List<SpuSaleAttr> spuSaleAttrs = spuService.getSpuSaleAttrListCheckBySku(spuId, skuId);
        return spuSaleAttrs;
    }

    @RequestMapping("getCategoryViewByCategory3Id/{category3Id}")
    BaseCategoryView getCategoryViewByCategory3Id(@PathVariable("category3Id") Long category3Id) {
        BaseCategoryView baseCategoryView = categoryService.getCategoryViewByCategory3Id(category3Id);

        return baseCategoryView;
    }
}
