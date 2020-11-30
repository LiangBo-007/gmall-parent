package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author LIANGBO
 * @since 2020-11-30
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuInfoController {
    /**
     * @description:
     * @return:
     * @time: 2020/11/30 14:52
     * @author: LIANG BO
     */
    @Autowired
    SpuInfoService spuInfoService;
    public Result getpage() {

    }
}

