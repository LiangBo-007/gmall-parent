package com.atguigu.gmall.product.service.Impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @time: 2020/12/1 16:10
 * @author: LIANGBO
 */
@Service
public class SkuserviceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;


    @Override
    public IPage<SkuInfo> skuList(IPage<SkuInfo> page) {
        page.setSize(20);
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(page, null);

        return skuInfoIPage;
    }

    //上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setIsSale(1);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);
        System.out.println("同步搜索引擎");
    }

    //下架
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setIsSale(0);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);
        System.out.println("同步搜索引擎");
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        Long sku_id = skuInfo.getId();
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (null != skuImageList) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(sku_id);
                skuImageMapper.insert(skuImage);
            }
        }
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (null != skuSaleAttrValueList) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(sku_id);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (null != skuAttrValueList) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(sku_id);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }
}
