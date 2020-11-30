package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @time: 2020/11/30 11:16
 * @author: LIANGBO
 */
@RequestMapping("admin/product")
@RestController
@CrossOrigin
public class SpuApiController {
    /**
     * @description:获取商品分页详情
     * @return:
     * @time: 2020/11/30 11:17
     * @author: LIANG BO
     */
    @RequestMapping("{page}/{limit}/{category3Id}")
    private Result getpage() {
        return null;
    }
}
