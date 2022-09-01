package com.huijixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huijixu.pojo.User;

import java.util.Map;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
public interface UserService extends IService<User> {

    /**
     * 查询验证码登录用户是否为新用户
     * @param map
     * @return
     */
    User isExit(Map map);
}
