package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class listController {

    @Autowired
    ListFeignClient listFeignClient;

    @RequestMapping("/")
    public String index(Model model) {

        List<JSONObject> list = listFeignClient.getBaseCategoryList();

        model.addAttribute("list", list);
        return "index/index";
    }

    @RequestMapping({"list.html", "search.html"})
    public String list(Model model, SearchParam searchParam) {

        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);
        model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
        model.addAttribute("goodsList", searchResponseVo.getGoodsList());
        return "list/index";
    }

}
