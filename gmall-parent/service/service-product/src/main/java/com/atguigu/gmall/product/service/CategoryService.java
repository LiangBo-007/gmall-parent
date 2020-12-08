package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.List;

/**
 * @description:
 * @time: 2020/11/28 20:13
 * @author: LIANGBO
 */
public interface CategoryService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);

    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    List<JSONObject> getBaseCategoryList();
}

