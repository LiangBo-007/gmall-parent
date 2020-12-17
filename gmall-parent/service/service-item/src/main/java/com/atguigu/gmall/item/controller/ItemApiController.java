package com.atguigu.gmall.item.controller;


import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/item")
public class ItemApiController {

    @Autowired
    ItemService itemService;


    public static void main(String[] args) throws IOException {
        String json = "{3|5|7\":8,\"3|4|7\":7,\"2|4|7\":6,\"1|6|7\":3,\"1|5|7\":1,\"2|6|7\":4,\"1|4|7\":2,\"2|5|7\":5,\"3|6|7\":9}";


        String spuId = "6";

        File file = new File("c:/spu_" + spuId + ".json");

        FileOutputStream fos = new FileOutputStream(file);

        fos.write(json.getBytes());
    }

    //商品页面信息封装Map
    @RequestMapping("getItem/{skuId}")
    Map<String, Object> getItem(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userId = request.getHeader("userId");
        Map<String, Object> map = itemService.getItem(skuId);

        return map;
    }
}
