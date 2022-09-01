package com.huijixu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huijixu.mapper.CategoryMapper;
import com.huijixu.pojo.Category;
import com.huijixu.pojo.Dish;
import com.huijixu.pojo.Setmeal;
import com.huijixu.service.CategoryService;
import com.huijixu.service.DishService;
import com.huijixu.service.SetmealService;
import com.huijixu.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Service
@SuppressWarnings({"all"})
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    //注入
    @Autowired
    private  DishService dishService;

    @Autowired
    private  SetmealService setmealService;



    /**
     * 检查关联关系，并删除某个分类
     * @param id
     */
    @Override
    public void remove(Long id) {

        //检查有无关联 套餐分类
        LambdaQueryWrapper<Setmeal> setmealWrapepr = new LambdaQueryWrapper<>();
        //eq方法封装sql语句条件    列；值
        setmealWrapepr.eq(Setmeal::getCategoryId,id);
        int count = setmealService.count(setmealWrapepr);
        //抛异常
        if (count > 0){
            throw new CustomException("已与存在套餐关联，不能删除！");
        }

        //检查有无关联 菜品分类
        LambdaQueryWrapper<Dish> dishWrapepr = new LambdaQueryWrapper<>();
        dishWrapepr.eq(Dish::getCategoryId,id);
        int count1 = dishService.count();
        //判断
        if (count1 > 0) {
            throw new CustomException("已与存在菜品关联，不能删除！");
        }

        //删除
        super.removeById(id);  //接口的接口方法IService

    }
}
