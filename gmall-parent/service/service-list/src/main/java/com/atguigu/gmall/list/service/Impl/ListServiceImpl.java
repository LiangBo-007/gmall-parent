package com.atguigu.gmall.list.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.repository.GoodsElasticsearchRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Autowired
    RedisTemplate redisTemplate;

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

    @Override
    public void hotScore(Long skuId) {
        // 将热度值更新进缓冲区
        Integer hotScoreRedis = (Integer) redisTemplate.opsForValue().get("hotScore:" + skuId);
        if (null != hotScoreRedis) {
            hotScoreRedis++;
            redisTemplate.opsForValue().set("hotScore:" + skuId, hotScoreRedis);
            if (hotScoreRedis % 10 == 0) {
                // 将热度值更新进es
                Goods goods = goodsElasticsearchRepository.findById(skuId).get();
                goods.setHotScore(Long.parseLong(hotScoreRedis + ""));
                goodsElasticsearchRepository.save(goods);
            }
        } else {
            redisTemplate.opsForValue().set("hotScore:" + skuId, 1);

        }

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

                //设置查询字段高亮显示
                Map<String, HighlightField> highlightFields = document.getHighlightFields();
                if (highlightFields != null && highlightFields.size() > 0) {
                    HighlightField HighlighTitle = highlightFields.get("title");
                    Text text = HighlighTitle.getFragments()[0];
                    String title = text.string();
                    goods.setTitle(title);
                }

            }
            //1.商品集合
            searchResponseVo.setGoodsList(list);
            //2.品牌
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
            //品牌信息集合
            searchResponseVo.setTrademarkList(searchResponseTmVos);


            //3.解析属性值聚合函数
            ParsedNested attrsAgg = (ParsedNested) searchResponse.getAggregations().get("attrsAge");
            ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrsAgg.getAggregations().get("attrIdAgg");
            //3.1取出属性值
            List<SearchResponseAttrVo> searchResponseAttrVos = attrIdAgg.getBuckets().stream().map(attrIdBucket -> {
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                // id
                long attrIdKey = attrIdBucket.getKeyAsNumber().longValue();
                // name
                ParsedStringTerms attrNameAgg = (ParsedStringTerms) attrIdBucket.getAggregations().get("attrNameAgg");
                String attrNameKey = attrNameAgg.getBuckets().get(0).getKeyAsString();
                // ValueList
                ParsedStringTerms attrValueAgg = (ParsedStringTerms) attrIdBucket.getAggregations().get("attrValueAgg");
                List<String> attrValueList = attrValueAgg.getBuckets().stream().map(attrValueBucket -> {
                    String attrValueKey = attrValueBucket.getKeyAsString();
                    return attrValueKey;
                }).collect(Collectors.toList());

                searchResponseAttrVo.setAttrId(attrIdKey);
                searchResponseAttrVo.setAttrName(attrNameKey);
                searchResponseAttrVo.setAttrValueList(attrValueList);
                return searchResponseAttrVo;
            }).collect(Collectors.toList());

            searchResponseVo.setAttrsList(searchResponseAttrVos);// 属性集合

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
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();// 属性id:属性值名称:属性名称
        String trademark = searchParam.getTrademark();// 商标id:商标名称
        String order = searchParam.getOrder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (!StringUtils.isEmpty(trademark)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId", trademark.split(":")[0]);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 关键字
        if (!StringUtils.isEmpty(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        // 属性
        if (null != props && props.length > 0) {
            for (String prop : props) {
                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);
                String attrValueName = split[1];
                String attrName = split[2];

                BoolQueryBuilder boolQueryBuilderNested = new BoolQueryBuilder();
                TermQueryBuilder termQueryBuilderAttrId = new TermQueryBuilder("attrs.attrId", attrId);
                MatchQueryBuilder matchQueryBuilderAttrValueName = new MatchQueryBuilder("attrs.attrValue", attrValueName);
                boolQueryBuilderNested.filter(termQueryBuilderAttrId);
                boolQueryBuilderNested.must(matchQueryBuilderAttrValueName);

                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", boolQueryBuilderNested, ScoreMode.None);

                boolQueryBuilder.filter(nestedQueryBuilder);

            }
        }
        //三级分类品牌查询信息
        if (null != category3Id && category3Id > 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            //term查詢
            boolQueryBuilder.filter(termQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
        //1.商标聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        //2.属性值聚合
        NestedAggregationBuilder nestedAttrAggregationBuilder = AggregationBuilders.nested("attrsAge", "attrs").subAggregation(
                AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));
        searchSourceBuilder.aggregation(nestedAttrAggregationBuilder);
        // 3.页面size
        searchSourceBuilder.size(20);
        searchSourceBuilder.from(0);
        //4.高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
        highlightBuilder.field("title");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 5.排序
        if (!StringUtils.isEmpty(order)) {
            String key = order.split(":")[0];
            String sort = order.split(":")[1];

            String sortName = "hotScore";

            if (key.equals("2")) {
                sortName = "price";
            }
            searchSourceBuilder.sort(sortName, sort.equals("asc") ? SortOrder.ASC : SortOrder.DESC);
        }

        System.out.println(searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchRequest.toString());
        return searchRequest;
    }
}


