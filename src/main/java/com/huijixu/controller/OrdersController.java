package com.huijixu.controller;

import com.huijixu.pojo.Orders;
import com.huijixu.service.OrdersService;
import com.huijixu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuiJiXu
 * @address QingDao China
 */

@SuppressWarnings({"All"})
@Slf4j
@RestController
@RequestMapping("order")

public class OrdersController {

    @Autowired
    OrdersService ordersService;

    @PostMapping("submit")
    public R<String> submit(
            @RequestBody
                    Orders orders
    ) {
        ordersService.submit(orders);
        return null;
    }


}
