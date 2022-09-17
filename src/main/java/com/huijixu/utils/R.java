package com.huijixu.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
//使用spring-cache ；redis 后，被序列化的对象要实现serializable接口
public class R<T>  implements Serializable {

    /**
     * 这里的状态码需要和前端也页面（HTML中方法的回调函数判断依据一致）
     */

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //  泛型数据

    //用Map的原因:
    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
