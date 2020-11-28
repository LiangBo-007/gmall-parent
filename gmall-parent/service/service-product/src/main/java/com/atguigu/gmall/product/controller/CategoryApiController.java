package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @time: 2020/11/28 19:18
 * @author: LIANGBO
 */
@RequestMapping("admin/product")
@RestController
public class CategoryApiController {
    /**
     * 查询一级分类信息
     */
    @RequestMapping("getCategory1")
    public Result getCategory1() {
        return Result.ok();
    }
}
