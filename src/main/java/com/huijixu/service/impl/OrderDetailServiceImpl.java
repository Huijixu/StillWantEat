package com.huijixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huijixu.mapper.OrderDetailMapper;
import com.huijixu.pojo.OrderDetail;
import com.huijixu.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper,OrderDetail> implements OrderDetailService {
}
