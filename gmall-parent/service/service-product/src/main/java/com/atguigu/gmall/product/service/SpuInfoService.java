package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @description:
 * @time: 2020/11/30 11:42
 * @author: LIANGBO
 */
public interface SpuInfoService {
    List<SpuInfo> getpage(Page<SpuInfo> page, Long category3Id);
}
