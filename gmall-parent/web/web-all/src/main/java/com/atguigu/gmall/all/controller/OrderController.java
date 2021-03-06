package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.client.OrderFengnClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
@CrossOrigin
public class OrderController {
    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    OrderFengnClient orderFeignClient;

    @RequestMapping("myOrder.html")
    public String myOrder() {
        return "order/myOrder";
    }

    @RequestMapping("trade.html")
    public String trade(HttpServletRequest request, Model model) {
        String userId = request.getHeader("userId");
        System.out.println(userId);
        List<OrderDetail> orderDetails = orderFeignClient.trade();
        List<UserAddress> userAddresses = userFeignClient.findUserAddressListByUserId(userId);
        model.addAttribute("userAddressList", userAddresses);
        model.addAttribute("detailArrayList", orderDetails);
        model.addAttribute("totalAmount", getTotalAmount(orderDetails));
        String tradeNo = orderFeignClient.genTradeNo(userId);
        // 后台生成tradeNo
        model.addAttribute("tradeNo", tradeNo);
        return "order/trade";

    }

    private BigDecimal getTotalAmount(List<OrderDetail> orderDetails) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetails) {
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            bigDecimal = bigDecimal.add(orderPrice);
        }
        return bigDecimal;
    }
}
