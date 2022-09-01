package com.huijixu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huijixu.mapper.OrdersMapper;
import com.huijixu.pojo.*;
import com.huijixu.service.*;
import com.huijixu.utils.BaseContext;
import com.huijixu.utils.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Service
@SuppressWarnings("All")
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     *
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders) {

        //orders 类中没有用户id所以先获取
        Long currentId = BaseContext.getCurrentId();
/**
 * 1
 * 购物车校验
 */
        //查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        //查到的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        //购物车为空
        if (shoppingCartList != null || shoppingCartList.size() == 0) {

            /**
             * 2
             地址信息校验
             通过orders中地址id查询地址详细信息并判断
             */
            User user = userService.getById(currentId);

            Long addressBookId = orders.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);
            if (addressBook == null) throw new CustomException("地址为空！");

            /**
             * 3
             * 逐个对Orders的属性赋值
             * 向订单表插入数据（一次就一个订单，所以是一条数据）
             */
            //生成并设置订单号
            long orderId = IdWorker.getId();
            orders.setNumber(String.valueOf(orderId));
            orders.setOrderTime(LocalDateTime.now());
            orders.setCheckoutTime(LocalDateTime.now());
            //订单状态
            orders.setStatus(2);

            /*
              总金额校验;生订单明细表
              amout()：计算总金额
             */
            AtomicInteger amount = new AtomicInteger(0);//初值为0
            //遍历购物车，计算总金额，封装明细表
            List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) ->
            {
                OrderDetail orderDetail = new OrderDetail();

                orderDetail.setOrderId(orderId);
                orderDetail.setNumber(item.getNumber());
                orderDetail.setDishFlavor(item.getDishFlavor());
                orderDetail.setDishId(item.getDishId());
                orderDetail.setSetmealId(item.getSetmealId());
                orderDetail.setName(item.getName());
                orderDetail.setImage(item.getImage());
                orderDetail.setAmount(item.getAmount());

                //金额计算
                amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

                return orderDetail;

            }).collect(Collectors.toList());


            orders.setAmount(new BigDecimal(amount.get()));   //为保证金额正确·，后端要校验前端计算结果
            orders.setUserId(currentId);
            orders.setNumber(String.valueOf(orderId));
            orders.setUserName(user.getName());
            orders.setConsignee(addressBook.getConsignee());
            orders.setPhone(addressBook.getPhone());

            /*
                三元运算符拼接地址
             */
            orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                    + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                    + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                    + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

            //保存购物车下数据
            this.save(orders);

            /**
             * 4
             * 将购物车书插入到订单明细表（订单明细中可以有多件商品，所以是多条数据）
             */
            orderDetailService.saveBatch(orderDetails);

            /**
             * 5
             *  清空购物车
             */
            shoppingCartService.remove(queryWrapper);

        } else {
            throw new CustomException("购物车为空");
        }

    }
}
