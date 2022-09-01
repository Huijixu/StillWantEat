package com.huijixu.dto;

import com.huijixu.pojo.OrderDetail;
import com.huijixu.pojo.Orders;
import lombok.Data;
import java.util.List;
/**
 * @author HuiJiXu
 * @address QingDao China
 */
/*
 * 订单
 */
@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    //收货人
    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
