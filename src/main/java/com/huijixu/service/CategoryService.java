package com.huijixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huijixu.pojo.Category;
import org.springframework.stereotype.Service;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
public interface CategoryService extends IService<Category> {

    /**
     * 检查有无关联关系，删除 某个分类
     */
    void remove(Long id);

}