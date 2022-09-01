package com.huijixu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Function;
import com.huijixu.mapper.UserMapper;
import com.huijixu.pojo.User;
import com.huijixu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {


    /**
     * 检查是否为新用户
     * @param map
     * @return
     */
    @Override
    public User isExit(Map map) {
        String phone = map.get("phone").toString();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = this.getOne(queryWrapper);
        //新用户
        if (user == null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            this.save(user);
        }
        return user;
    }
}