package com.atguigu.gmall.list.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.repository.GoodsElasticsearchRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    GoodsElasticsearchRepository goodsElasticsearchRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired


    @Override
    public List<JSONObject> getBaseCategoryList() {
        return productFeignClient.getBaseCategoryList();
    }

    /**
     * @description:搜索上架
     * @return:
     * @time: 2020/12/9 18:48
     * @author: LIANG BO
     */
    @Override
    public void onSale(Long skuId) {
        //创建尚品
        Goods goods = new Goods();
        //sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
        //属性数据
        List<SearchAttr> searchAttrs = productFeignClient.getSearchAttrList(skuId);
        //商标数据
        BaseTrademark baseTrademark = productFeignClient.getTrademarkById(skuInfo.getTmId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setAttrs(searchAttrs);
        goods.setCategory3Id(skuInfo.getCategory3Id());
        goods.setHotScore(0L);
        goods.setCreateTime(new Date());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setTmId(skuInfo.getTmId());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        goods.setTmName(baseTrademark.getTmName());
        goods.setId(skuId);
        goodsElasticsearchRepository.save(goods);
    }


    /**
     * @description:创建商品索引ES数据结构
     * @return:
     * @time: 2020/12/9 18:49
     * @author: LIANG BO
     */
    @Override
    public void createGoodsIndex() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
    }

    @Override
    public void cancelSale(Long skuId) {
        goodsElasticsearchRepository.deleteById(skuId);
    }

}


