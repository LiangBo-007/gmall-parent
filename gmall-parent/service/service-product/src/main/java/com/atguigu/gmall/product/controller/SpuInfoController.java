package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    SpuInfoService spuInfoService;

    /**
     * @description:
     * @return:
     * @time: 2020/11/30 14:52
     * @author: LIANG BO
     */
    @RequestMapping("{pageNo}/{size}")
    private Result getpage(
            @PathVariable("PageNo") Long PageNo,
            @PathVariable("size") Long size,
            @PathVariable("category3Id") Long category3Id
    ) {
        IPage<SpuInfo> Page = new Page<>();
        Page.setSize(size);
        Page.setCurrent(PageNo);
        IPage<SpuInfo> getpage = spuInfoService.getpage(Page,category3Id);
        return Result.ok(getpage);
    }
}

