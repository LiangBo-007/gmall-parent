package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性表 服务类
 * </p>
 *
 * @author LIANGBO
 * @since 2020-11-29
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    List<BaseAttrInfo> attrInfoList(Long category3Id);

    List<SearchAttr> getSearchAttrList(Long skuId);
}
