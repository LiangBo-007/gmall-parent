package com.atguigu.gmall.product.service.Impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;


    @Override
    public IPage<SpuInfo> spuList(IPage<SpuInfo> page, Long category3Id) {

        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("category3_id", category3Id);

        IPage<SpuInfo> infoIPage = spuInfoMapper.selectPage(page, queryWrapper);

        return infoIPage;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {


        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        spuInfoMapper.insert(spuInfo);
        Long spu_id = spuInfo.getId();


        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (null != spuImageList) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spu_id);
                spuImageMapper.insert(spuImage);
            }
        }

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();

        if (null != spuSaleAttrList) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spu_id);
                spuSaleAttrMapper.insert(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (null != spuSaleAttrValueList) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spu_id);
                        spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());// spuId+销售属性id联合外键
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    /**
     * @description:
     * @return:
     * @time: 2020/12/1 18:38
     * @author: LIANG BO
     */
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(queryWrapper);
        return spuImages;
    }

    /**
     * @description:
     * @return:
     * @time: 2020/12/1 18:38
     * @author: LIANG BO
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(queryWrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            QueryWrapper<SpuSaleAttrValue> queryWrapperValue = new QueryWrapper<>();
            queryWrapperValue.eq("spu_id", spuSaleAttr.getSpuId());
            queryWrapperValue.eq("base_sale_attr_id", spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(queryWrapperValue);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);

        }
        return spuSaleAttrs;
    }
}

