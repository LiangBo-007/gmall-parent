package com.atguigu.gmall.product.service.Impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrademarkServiceImpl implements TrademarkService {

    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public List<BaseTrademark> baseTrademark() {

         return baseTrademarkMapper.selectList(null);
    }
}
