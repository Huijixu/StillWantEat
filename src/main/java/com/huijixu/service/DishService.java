package com.huijixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huijixu.dto.DishDto;
import com.huijixu.pojo.Dish;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品
     * 向两张表 dish,dish_flavor插入数据
     *
     * @param dishDto
     */
    void saveDishAndFlavor(DishDto dishDto);


    DishDto getByIdWithDishFlavors(Long id );


    /**
     * 保存菜品修改内容
     * @param dishDto
     */
    void updateDishAndFlavor(DishDto dishDto);

}