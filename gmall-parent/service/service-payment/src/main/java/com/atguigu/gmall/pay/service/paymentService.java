package com.atguigu.gmall.pay.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.Map;

/**
 * @description:
 * @time: 2020/12/19 14:57
 * @author: LIANGBO
 */
public interface paymentService {
    void savePaymentInfo(PaymentInfo paymentInfo);

    String alipaySubmit(OrderInfo orderInfoById);

    void updatePayment(PaymentInfo paymentInfo);

    Map<String, Object> query(String out_trade_no);

    Map<String, Object> checkPayment(String out_trade_no);
}
