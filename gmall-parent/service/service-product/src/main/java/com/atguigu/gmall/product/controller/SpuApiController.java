package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @time: 2020/11/30 11:16
 * @author: LIANGBO
 */
@RequestMapping("admin/product")
@RestController
@CrossOrigin
public class SpuApiController {
    @Autowired
    SpuInfoService spuInfoService;

    /**
     * @description:获取商品分页详情
     * @return:
     * @time: 2020/11/30 11:17
     * @author: LIANG BO
     */
    @RequestMapping("{pageNo}/{size}")
    private Result getpage(
            @PathVariable("PageNo") Long PageNo,
            @PathVariable("size") Long size,
            @PathVariable("category3Id") Long category3Id
    ) {
        Page<SpuInfo> Page = new Page<>();
        Page.setSize(size);
        Page.setCurrent(PageNo);
        List<SpuInfo> spuInfoList = spuInfoService.getpage(Page, category3Id);
        return Result.ok(spuInfoList);
    }
}
