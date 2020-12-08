package com.atguigu.gmall.list;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    ListService listService;

    @RequestMapping("getBaseCategoryList")
    List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list = listService.getBaseCategoryList();

        return list;
    }
}