package com.atguigu.gmall.product.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.config.GmallCache;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @description:
 * @time: 2020/11/28 22:08
 * @author: LIANGBO
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    /*
     * 查询一级信息
     * */
    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> baseCategory1s = (List<BaseCategory1>) baseCategory1Mapper.selectList(null);
        return baseCategory1s;
    }

    //查询二级信息
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        QueryWrapper<BaseCategory2> queryWrapper = wrapper.eq("category1_id", category1Id);
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(queryWrapper);
        return baseCategory2s;
    }

    //查询三级信息
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        QueryWrapper<BaseCategory3> queryWrapper = wrapper.eq("category2_id", category2Id);
        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(queryWrapper);
        return baseCategory3s;
    }

    /**
     * @description:
     * @return: 目录信息
     * @time: 2020/12/2 23:04
     * @author: LIANG BO
     */
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        QueryWrapper<BaseCategoryView> baseCategoryViewQueryWrapper = new QueryWrapper<>();
        baseCategoryViewQueryWrapper.eq("category3_id", category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(baseCategoryViewQueryWrapper);

        return baseCategoryView;
    }

    @Override
    @GmallCache
    public List<JSONObject> getBaseCategoryList() {
        //查询所有分类的信息
        List<BaseCategoryView> categoryViews = baseCategoryViewMapper.selectList(null);
        // 将CategoryList 转化为JSONObject
        List<JSONObject> list = new ArrayList<>();
        Map<Long, List<BaseCategoryView>> categroy1Map = categoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //获取一级分类信息
        for (Map.Entry<Long, List<BaseCategoryView>> longListEntry : categroy1Map.entrySet()) {
            Long categroy1Object = longListEntry.getKey();
            String category1Name = longListEntry.getValue().get(0).getCategory1Name();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("categoryId", categroy1Object);
            jsonObject.put("categoryName", category1Name);

            //二级分类集合查询
            ArrayList<Object> category2list = new ArrayList<>();
            List<BaseCategoryView> categoryViews2 = longListEntry.getValue();
            Map<Long, List<BaseCategoryView>> categroy2Map = categoryViews2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> listEntry : categroy2Map.entrySet()) {
                Long listEntryKey2 = listEntry.getKey();
                String category2Name = listEntry.getValue().get(0).getCategory2Name();
                JSONObject categroy2Json = new JSONObject();
                categroy2Json.put("categoryId", listEntryKey2);
                categroy2Json.put("categoryName", category2Name);

                //三级集合
                List<JSONObject> category3list = new ArrayList<>();
                List<BaseCategoryView> category3Views = listEntry.getValue();
                Map<Long, List<BaseCategoryView>> longListMap3 = category3Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> categroy3Map : longListMap3.entrySet()) {
                    Long categroy3Id = categroy3Map.getKey();
                    String category3Name = categroy3Map.getValue().get(0).getCategory3Name();
                    JSONObject categroy3Json = new JSONObject();
                    categroy3Json.put("categoryId", categroy3Id);
                    categroy3Json.put("categoryName", category3Name);
                    category3list.add(categroy3Json);
                }
                categroy2Json.put("categoryChild", category3list);
                category2list.add(categroy2Json);
            }
            jsonObject.put("categoryChild", category2list);
            list.add(jsonObject);

        }
        return list;

    }

}