package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
public class OrderController {

    @RequestMapping("myOrder.html")
    public String myOrder() {
        return "order/myOrder";
    }

}
