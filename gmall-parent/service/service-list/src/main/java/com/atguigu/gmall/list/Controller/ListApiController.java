package com.atguigu.gmall.list.Controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    ListService listService;

    @RequestMapping("getBaseCategoryList")
    List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list = listService.getBaseCategoryList();

        return list;
    }

  //上架同步搜索
  @RequestMapping("onSale/{skuId}")
  void onSale(@PathVariable("skuId")  Long skuId){

      listService.onSale(skuId);

      System.out.println("搜索系统上架商品");
  }


    @RequestMapping("cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId){

        listService.cancelSale(skuId);

        System.out.println("搜索系统下架商品");
    }


    //创建索引
    @RequestMapping("createGoodsIndex")
    Result createGoodsIndex() {
        listService.createGoodsIndex();
        return Result.ok();
    }

}
