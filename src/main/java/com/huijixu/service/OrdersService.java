package com.huijixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huijixu.pojo.Orders;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
public interface OrdersService extends IService<Orders>  {

    void submit(Orders orders);
}
