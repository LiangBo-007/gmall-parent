package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @description:
 * @time: 2020/11/30 11:42
 * @author: LIANGBO
 */
public interface SpuInfoService {
    IPage<SpuInfo> getpage(IPage<SpuInfo> page, Long category3Id);
}
