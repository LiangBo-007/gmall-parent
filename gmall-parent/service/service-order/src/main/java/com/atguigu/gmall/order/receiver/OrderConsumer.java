package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.mq.service.RabbitService;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.ware.bean.WareOrderTask;
import com.atguigu.gmall.ware.bean.WareOrderTaskDetail;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class OrderConsumer {

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitService rabbitService;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange.payment.pay", autoDelete = "false"),
            value = @Queue(value = "queue.payment.pay", autoDelete = "false"),
            key = {"routing.payment.pay"}
    ))
    public void orderConsume(Channel channel, Message message, String json) throws IOException {


        PaymentInfo paymentInfo = JSON.parseObject(json, PaymentInfo.class);

        System.out.println("订单服务消费支付成功队列");

        // 更新订单状态，已支付
        Long orderId = orderService.updateOrderPay(paymentInfo);

        if (null != orderId && orderId > 0) {
            // 给库存发通知，锁定商品
            OrderInfo orderInfo = orderService.getOrderInfoById(orderId);
            List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

            WareOrderTask wareOrderTask = new WareOrderTask();
            wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
            wareOrderTask.setPaymentWay(orderInfo.getPaymentWay());
            wareOrderTask.setCreateTime(new Date());
            wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
            wareOrderTask.setConsignee(orderInfo.getConsignee());
            wareOrderTask.setOrderId(orderId + "");
            List<WareOrderTaskDetail> wareOrderTaskDetails = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetailList) {
                WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();

                wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
                wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
                wareOrderTaskDetail.setSkuId(orderDetail.getSkuId() + "");
                wareOrderTaskDetails.add(wareOrderTaskDetail);
            }
            wareOrderTask.setDetails(wareOrderTaskDetails);

            rabbitService.sendMessage("exchange.direct.ware.stock", "ware.stock", JSON.toJSONString(wareOrderTask));

        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
