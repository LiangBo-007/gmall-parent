package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.pay.config.AlipayConfig;
import com.atguigu.gmall.pay.mapper.PaymentInfoMapper;
import com.atguigu.gmall.pay.service.paymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @time: 2020/12/19 18:58
 * @author: LIANGBO
 */
@Service
public class paymentServiceImpl implements paymentService {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insert(paymentInfo);
    }

    //向支付宝提交支付信息
    @Override
    public String alipaySubmit(OrderInfo orderInfoById) {
        //支付成功后同步及异步跳转
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        payRequest.setReturnUrl(AlipayConfig.return_payment_url);
        payRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", orderInfoById.getOutTradeNo());
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", 0.01);
        map.put("subject", orderInfoById.getOrderDetailList().get(0).getSkuName());
        payRequest.setBizContent(JSON.toJSONString(map));
        AlipayTradePagePayResponse response = null;
        try {
            response = alipayClient.pageExecute(payRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //获取需提交的form表单
        String submitFormData = response.getBody();
        //客户端拿到submitFormData做表单提交
        return submitFormData;
    }

    //保存回调信息
    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", paymentInfo.getOutTradeNo());
        paymentInfoMapper.update(paymentInfo, queryWrapper);
    }

    @Override
    public Map<String, Object> query(String out_trade_no) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", out_trade_no);
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        boolean success = response.isSuccess();// 交易是否创建
        if (success) {
            System.out.println("调用成功");
            map.put("trade_status", response.getTradeStatus());
        } else {
            System.out.println("调用失败");
        }
        return map;
    }

    @Override
    public Map<String, Object> checkPayment(String out_trade_no) {
        return query(out_trade_no);
    }
}
