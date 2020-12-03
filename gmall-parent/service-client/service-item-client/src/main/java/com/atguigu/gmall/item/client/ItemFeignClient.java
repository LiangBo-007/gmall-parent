package com.atguigu.gmall.item.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(value = "service-item")
public interface ItemFeignClient {
    /**
     * @description:根据skuid前端页面数据
     * @return:
     * @time: 2020/12/2 22:12
     * @author: LIANG BO
     */
    @RequestMapping("api/item/getItem/{skuId}")
    Map<String, Object> getItem(@PathVariable("skuId") Long skuId);
}
