package com.atguigu.gmall.order.service.Impl;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @time: 2020/12/18 19:59
 * @author: LIANGBO
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    //判断订单是否重复
    @Override
    public boolean checkTradeNo(String userId, String tradeNo) {
        boolean judeg = false;
        String tradeNoFormCache = (String) redisTemplate.opsForValue().get("user:" + userId + ":tradeCode");
        if (!StringUtils.isEmpty(tradeNoFormCache) && tradeNoFormCache.equals(tradeNo)) {
            redisTemplate.delete("user:" + userId + ":tradeCode");
            judeg = true;
        }
        return judeg;
    }

    @Override
    public String submitOrder(OrderInfo order) {
        //设置保存订单信息
        order.setProcessStatus(ProcessStatus.UNPAID.getComment());
        order.setOrderStatus(OrderStatus.UNPAID.getComment());
        //设置订单日期信息
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.add(1, Calendar.DATE);
        order.setExpireTime(instance.getTime());
        order.setCreateTime(date);
        //外部单号
        String outTradeNo = "atgugiu";
        outTradeNo += System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        outTradeNo += sdf.format(date);
        order.setOutTradeNo(outTradeNo);
        order.setOrderComment("世界真好");
        order.setTotalAmount(getTotalAmount(order.getOrderDetailList()));
        orderInfoMapper.insert(order);
        Long orderId = order.getId();

        List<OrderDetail> orderDetailList = order.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insert(orderDetail);
        }
        return order + "";
    }

    //添加下单信息缓存
    @Override
    public String genTradeNo(String userId) {
        String tradeNo = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("user:" + userId + ":tradeCode", tradeNo);
        return tradeNo;
    }

    //商品价格计算
    private BigDecimal getTotalAmount(List<OrderDetail> orderDetailList) {

        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            totalAmount = totalAmount.add(orderPrice);
        }
        return totalAmount;
    }
}
