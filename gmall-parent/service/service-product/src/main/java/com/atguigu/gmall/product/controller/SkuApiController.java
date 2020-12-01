package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @time: 2020/12/1 15:03
 * @author: LIANGBO
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SkuApiController {
    @Autowired
    SkuService skuService;


    /**
     * @description:
     * @return:
     * @time: 2020/12/1 19:04
     * @author: LIANG BO
     */
    @RequestMapping("list/{page}/{limit}")
    public Result skuList(@PathVariable("page") Long page, @PathVariable("limit") Long limit) {
        IPage<SkuInfo> pages = new Page<>();
        pages.setSize(limit);
        pages.setCurrent(page);
        IPage<SkuInfo> skuInfoIPage = skuService.skuList(pages);
        return Result.ok(skuInfoIPage);
    }

    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        skuService.onSale(skuId);
        return Result.ok();
    }

    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        skuService.cancelSale(skuId);
        return Result.ok();
    }
}
