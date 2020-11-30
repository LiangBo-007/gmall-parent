package com.atguigu.gmall.product.service.Impl;

import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LIANG
 * @version 1.0
 * @date 2020/11/30 0030-下午 12:21
 */
@Service
public class SpuInfoServiceImpl implements SpuInfoService {
    @Autowired
    SpuInfoMapper spuInfoMapper;

    /*
     * spu列表信息查询
     * */
    @Override
    public List<SpuInfo> getpage(Page<SpuInfo> page, Long category3Id) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        List<SpuInfo> spuInfos = spuInfoMapper.selectList(queryWrapper);
        return spuInfos;
    }
}
