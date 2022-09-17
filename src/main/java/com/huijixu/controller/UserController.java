package com.huijixu.controller;

import com.huijixu.pojo.User;
import com.huijixu.service.UserService;
import com.huijixu.utils.R;
import com.huijixu.utils.SMSUtils;
import com.huijixu.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author HuiJiXu
 * @address QingDao China
 */

@RestController
@RequestMapping("/user")
@Slf4j
@SuppressWarnings({"all"})
public class UserController {

    @Autowired
    private UserService userService;

    //reids
    @Autowired
    private RedisTemplate redisTemplate ;

    /**
     * 后端调用第三方短信服务向手机发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(
            @RequestBody
                    User user,
            HttpSession session
    ) {
        //接受用户端的手机号，发送验证码
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成5位验证码
            String ValidateCode = ValidateCodeUtils.generateValidateCode(5).toString();
            log.info("ValidateCode={}",ValidateCode);

            //将验证码交给redis接管,设置两分钟有效
            redisTemplate.opsForValue().set(phone,ValidateCode,2, TimeUnit.MINUTES);

            //调用第三方API服务
//            SMSUtils.sendMessage("第三方服务的签名验证码",ValidateCode,phone,"参数");

            session.setAttribute(phone, ValidateCode); //这里验证码在session域中的索引采用对应的手机号
            return R.success("发送验证码成功！");
        }

        return R.error("获取验证码失败");
    }


    /**
     * 用户端登录
     * @return
     */
    @PostMapping("/login")
    public R<String> login(
            @RequestBody
                    Map map,
            HttpSession session
    ) {
        log.info("Map={}",map.toString());
        //拿到前端 输入用户手机号，验证码
        String phone = (String) map.get("phone");
        String ValidateCode = (String) map.get("code");

        //从redis拿到后端验证码
        String sessionCode = (String) redisTemplate.opsForValue().get(phone);

        //比对
        //用户输入为空
        if ( !sessionCode.equals(ValidateCode)  || ValidateCode == null ){
            return R.error("验证码错误");
        }
        //一致则登录成功，新用户自动注册到数据库
        User user = userService.isExit(map);

        session.setAttribute("user",user.getId());

        //登录成功后将在redis设置将validcode失效
        redisTemplate.delete(phone);
        return R.success(user.toString());
    }


}
