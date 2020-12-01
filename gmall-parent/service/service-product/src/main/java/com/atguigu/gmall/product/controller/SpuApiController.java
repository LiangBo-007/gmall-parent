package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuApiController {

    @Autowired
    SpuService spuService;

    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }


    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrs = spuService.baseSaleAttrList();
        return Result.ok(baseSaleAttrs);
    }

    @RequestMapping("{pageNo}/{size}")
    public Result spuList(@PathVariable("pageNo") Long pageNo, @PathVariable("size") Long size, Long category3Id) {

        IPage<SpuInfo> page = new Page<>();

        page.setSize(size);
        page.setCurrent(pageNo);

        IPage<SpuInfo> spuInfoIPage = spuService.spuList(page, category3Id);
        return Result.ok(spuInfoIPage);
    }

    /**
     * @description:查询图片信息
     * @return:
     * @time: 2020/12/1 17:23
     * @author: LIANG BO
     */
    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId) {
        List<SpuImage> spuImages = spuService.spuImageList(spuId);
        return Result.ok(spuImages);
    }

    /**
     * @description:根据spuId获取销售属性
     * @return:
     * @time: 2020/12/1 17:24
     * @author: LIANG BO
     */
    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId) {
        List<SpuSaleAttr> spuSaleList = spuService.spuSaleAttrList(spuId);
        return Result.ok(spuSaleList);
    }
}
