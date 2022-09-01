package com.huijixu.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
/**
 * @author HuiJiXu
 * @address QingDao China
 */

/*
元数据处理器
*/

@Slf4j
@Component      //配置bean注解
public class MyMetaObjectHandler implements MetaObjectHandler {



    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("字段自动填充-[insert]");

        //线程信息
        Long thread = Thread.currentThread().getId();
        log.info("MetaHandler-insert 线程id:{}",thread);

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("字段自动填充-[update]");

        //线程信息
        Long thread = Thread.currentThread().getId();
        log.info("MetaHandler-update 线程id:{}",thread);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());


    }
}
