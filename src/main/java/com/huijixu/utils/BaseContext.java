package com.huijixu.utils;

/**
 * @author huijixu
 * @version 1.0
 * @since 2022.1
 */

/**
 * 本类为了解决utils.MyMetaObjectHandler无法直接获取当前登录用户ID进行字段自动赋值
 */
public class BaseContext {

    private static  ThreadLocal<Long>  threadLocal = new ThreadLocal<>();

    //设置当前登录用户id
    public  static  void setCurrentId(Long id){
        threadLocal.set(id);
    }

    //获取当前ThreadLocal 的一个线程对应的登录用户的id
    public  static  Long getCurrentId(){
        return threadLocal.get();
    }
}
