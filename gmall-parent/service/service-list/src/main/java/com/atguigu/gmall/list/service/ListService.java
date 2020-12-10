package com.atguigu.gmall.list.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ListService {
    List<JSONObject> getBaseCategoryList();

    void onSale(Long skuId);

    void createGoodsIndex();

    void cancelSale(Long skuId);
}
