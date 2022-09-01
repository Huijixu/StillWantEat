package com.huijixu.utils;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})   // 反射捕获路由映射和Controller方法的异常
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     *  处理重复录入异常
     *      如员工名重复
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public  R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage()); //获取异常信息
        if (exception.getMessage().contains("Duplicate entry"))  {              //jdk抛出的重复异常
            String[] s = exception.getMessage().split(" ");
            String msg = s[2] + "已经存在";  // 这个是看jdk输出的异常信息在该段异常信息里是第几个单词
            return  R.error(msg);
        }
        return  R.error("未知错误！");
    }


    /**
     *  用户非法删除异常
     *       如删除某个具有关联关系的菜品时
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public  R<String> exceptionHandler(CustomException exception){
        log.error(exception.getMessage());
        return  R.error(exception.getMessage());
    }
}
