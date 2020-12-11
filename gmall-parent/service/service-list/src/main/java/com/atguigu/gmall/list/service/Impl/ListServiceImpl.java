package com.atguigu.gmall.list.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.repository.GoodsElasticsearchRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    GoodsElasticsearchRepository goodsElasticsearchRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public List<JSONObject> getBaseCategoryList() {
        return productFeignClient.getBaseCategoryList();
    }

    /**
     * @description:搜索上架
     * @return:
     * @time: 2020/12/9 18:48
     * @author: LIANG BO
     */
    @Override
    public void onSale(Long skuId) {
        //创建尚品
        Goods goods = new Goods();
        //sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuId);
        //属性数据
        List<SearchAttr> searchAttrs = productFeignClient.getSearchAttrList(skuId);
        //商标数据
        BaseTrademark baseTrademark = productFeignClient.getTrademarkById(skuInfo.getTmId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setAttrs(searchAttrs);
        goods.setCategory3Id(skuInfo.getCategory3Id());
        goods.setHotScore(0L);
        goods.setCreateTime(new Date());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setTmId(skuInfo.getTmId());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        goods.setTmName(baseTrademark.getTmName());
        goods.setId(skuId);
        goodsElasticsearchRepository.save(goods);
    }


    /**
     * @description:创建商品索引ES数据结构
     * @return:
     * @time: 2020/12/9 18:49
     * @author: LIANG BO
     */
    @Override
    public void createGoodsIndex() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
    }

    @Override
    public void cancelSale(Long skuId) {
        goodsElasticsearchRepository.deleteById(skuId);
    }

    //同步搜索
    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        //返回結果集
        SearchResponse searchResponse = null;
        //查詢結果信息
        SearchRequest searchRequest = getSearchRequest(searchParam);
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchResponseVo searchResponseVo = parseSearchResponse(searchResponse);
        return searchResponseVo;
    }

    //創建查詢條件集
    private SearchResponseVo parseSearchResponse(SearchResponse searchResponse) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();
        if (null != hitsHits && hitsHits.length > 0) {
            ArrayList<Goods> list = new ArrayList<>();
            for (SearchHit document : hitsHits) {
                String goodsJson = document.getSourceAsString();
                Goods goods = JSON.parseObject(goodsJson, Goods.class);
                list.add(goods);
            }
            //商品集合
            searchResponseVo.setGoodsList(list);
            //品牌
            ArrayList<SearchResponseTmVo> searchResponseTmVos = new ArrayList<>();
            //获取商品分类信息接口实现类集合信息
            ParsedLongTerms tmIdAgg = (ParsedLongTerms) searchResponse.getAggregations().get("tmIdAgg");
            //获取品牌id集合key信息
            List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
                long value = bucket.getKeyAsNumber().longValue();
                //获取品牌名称
                ParsedStringTerms nameAgg = (ParsedStringTerms) bucket.getAggregations().get("tmNameAgg");
                List<? extends Terms.Bucket> nameAggBuckets = nameAgg.getBuckets();
                String nameKey = nameAggBuckets.get(0).getKeyAsString();
                //获取url信息
                ParsedStringTerms urlAgg = (ParsedStringTerms) bucket.getAggregations().get("tmLogoUrlAgg");
                List<? extends Terms.Bucket> urlAggBuckets = urlAgg.getBuckets();
                String urlKey = urlAggBuckets.get(0).getKeyAsString();

                searchResponseTmVo.setTmId(value);
                searchResponseTmVo.setTmName(nameKey);
                searchResponseTmVo.setTmLogoUrl(urlKey);

                searchResponseTmVos.add(searchResponseTmVo);
            }

            searchResponseVo.setTrademarkList(searchResponseTmVos);

        }
        return searchResponseVo;
    }





    //創建查詢結果集
    private SearchRequest getSearchRequest(SearchParam searchParam) {
        //建立請求
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.indices("goods");
        searchRequest.types("info");
        Long category3Id = searchParam.getCategory3Id();
        //获取属性关键信息
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();
        //三级分类品牌查询信息
        if (null != category3Id && category3Id > 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            //term查詢
            searchSourceBuilder.query(termQueryBuilder);
        }
        //商标聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchRequest.source(searchSourceBuilder);

        System.out.println(searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchRequest.toString());
        return searchRequest;
    }
}


