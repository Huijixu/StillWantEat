package com.huijixu.pojo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author HuiJiXu
 * @address QingDao China
 */

/*
 * 订单明细
 */
@Data
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    //菜品/套餐 名称  （非人名）
    private String name;

    //订单id
    private Long orderId;

    //菜品id
    private Long dishId;

    //套餐id
    private Long setmealId;

    //口味
    private String dishFlavor;

    //数量
    private Integer number;

    //金额
    private BigDecimal amount;

    //图片
    private String image;
}
