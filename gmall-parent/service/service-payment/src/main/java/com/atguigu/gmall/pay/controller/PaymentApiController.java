package com.atguigu.gmall.pay.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFengnClient;
import com.atguigu.gmall.pay.service.paymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @time: 2020/12/19 14:55
 * @author: LIANGBO
 */
@RestController
@RequestMapping("api/payment")
public class PaymentApiController {
    @Autowired
    paymentService paymentService;
    @Autowired
    OrderFengnClient orderFengnClient;


    //支付成功回调函数(同步)
    @RequestMapping("alipay/callback/return")
    //保存回调信息
    public String callbackReturn(HttpServletRequest request) {
        //获取回调信息
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String callback_content = request.getQueryString();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.toString());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(callback_content);
        return "<form action=\"http://payment.gmall.com/paySuccess.html\">\n" +
                "</form>\n" +
                "<script>\n" +
                "document.forms[0].submit();\n" +
                "</script>";
    }

    //支付成功异步回调(支付宝内部调用)
    @RequestMapping("alipay/callback/notify")
    public String callbackNotify() {
        return null;
    }

    //支付订单提交
    @RequestMapping("alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId") Long orderId) {
        //查询获取订单信息
        OrderInfo orderInfoById = orderFengnClient.getOrderInfoById(orderId);
        //设置支付提交表单信息
        String form = paymentService.alipaySubmit(orderInfoById);

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        paymentInfo.setOutTradeNo(orderInfoById.getOutTradeNo());
        paymentInfo.setPaymentType("在线支付");
        paymentInfo.setOrderId(orderId);
        paymentInfo.setTotalAmount(orderInfoById.getTotalAmount());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setSubject(orderInfoById.getOrderDetailList().get(0).getSkuName());
        paymentService.savePaymentInfo(paymentInfo);
        return form;
    }

    @RequestMapping(value = "alipay/query/{out_trade_no}")
    public Result query(@PathVariable("out_trade_no") String out_trade_no) {
        Map<String, Object> map = paymentService.query(out_trade_no);
        return Result.ok(map);
    }
}
