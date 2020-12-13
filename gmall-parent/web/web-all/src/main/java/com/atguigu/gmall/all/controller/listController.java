package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class listController {

    @Autowired
    ListFeignClient listFeignClient;

    @RequestMapping({"list.html","search.html"})
    public String list(Model model, SearchParam searchParam, HttpServletRequest request){
        String urlParam = getUrlParam(searchParam, request);
        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);

        if(null!=searchResponseVo.getGoodsList()&&searchResponseVo.getGoodsList().size()>0){
            model.addAttribute("goodsList",searchResponseVo.getGoodsList());
            model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
            model.addAttribute("attrsList",searchResponseVo.getAttrsList());
            model.addAttribute("urlParam",urlParam);
        }
        if(!StringUtils.isEmpty(searchParam.getTrademark())){

            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }
        if(null!=searchParam.getProps()&&searchParam.getProps().length>0){
            List<SearchAttr> searchAttrs = new ArrayList<>();
            for (String prop : searchParam.getProps()) {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrName(prop.split(":")[2]);
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttrs.add(searchAttr);
            }
            model.addAttribute("propsParamList",searchAttrs);
        }

        if(!StringUtils.isEmpty(searchParam.getOrder())){
            Map<String,String> orderMap = new HashMap<>();
            orderMap.put("type",searchParam.getOrder().split(":")[0]);
            orderMap.put("sort",searchParam.getOrder().split(":")[1]);

            model.addAttribute("orderMap",orderMap);
        }

        return "list/index";
    }

    private String getUrlParam(SearchParam searchParam, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        StringBuffer urlParam = new StringBuffer(requestURI);
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();// 属性id:属性值名称:属性名称
        String trademark = searchParam.getTrademark();// 商标id:商标名称

        if(!StringUtils.isEmpty(keyword)){
            urlParam.append("?keyword="+keyword);
        }

        if(null!=category3Id&&category3Id>0){
            urlParam.append("?category3Id="+category3Id);
        }

        if(null!=props&&props.length>0){
            for (String prop : props) {
                urlParam.append("&props="+prop);
            }
        }

        if(!StringUtils.isEmpty(trademark)){
            urlParam.append("&trademark="+trademark);
        }

//       if(!StringUtils.isEmpty(searchParam.getOrder())){
//           urlParam.append("&order="+searchParam.getOrder());
//       }

        return urlParam.toString();
    }


    @RequestMapping("/")
    public String index(Model model){

        List<JSONObject> list =  listFeignClient.getBaseCategoryList();

        model.addAttribute("list",list);
        return "index/index";
    }

}
