package com.atguigu.gmall.product.service.Impl;


import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 属性表 服务实现类
 * </p>
 *
 * @author LIANGBO
 * @since 2020-11-29
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo> implements BaseAttrInfoService {
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    /**
     * @description:查询信息
     * @return:
     * @time: 2020/11/29 13:45
     * @author: LIANG BO
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category3Id) {

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectAttrInfoList(3, category3Id);
        /*QueryWrapper<BaseAttrInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_level", 3);
        queryWrapper.eq("category_id", category3Id);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectList(queryWrapper);

        for (BaseAttrInfo baseAttrInfo : baseAttrInfoList) {
            Long attr_id = baseAttrInfo.getId();
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", attr_id);
            List<BaseAttrValue> baseAttrInfos = baseAttrValueMapper.selectList(wrapper);
            baseAttrInfo.setAttrValueList(baseAttrInfos);
*/
        return baseAttrInfos;
    }

    @Override
    public List<SearchAttr> getSearchAttrList(Long skuId) {
        List<SearchAttr> searchAttrs = baseAttrInfoMapper.selectSearchAttrList(skuId);
        return searchAttrs;
    }

    /**
     * @description:保存和修改信息
     * @return:
     * @time: 2020/11/29 13:45
     * @author: LIANG BO
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        Long id = baseAttrInfo.getId();

        if (id == null || id <= 0) {
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            Long attr_id = baseAttrInfo.getId();
            id = attr_id;
        } else {
            baseAttrInfoMapper.updateById(baseAttrInfo);
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", id);
            baseAttrValueMapper.delete(queryWrapper);
        }

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }

    /**
     * @description:查询属性信息
     * @return:
     * @time: 2020/11/29 14:01
     * @author: LIANG BO
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id", attrId);
        List<BaseAttrValue> baseAttrValues = baseAttrValueMapper.selectList(queryWrapper);
        return baseAttrValues;
    }
}
