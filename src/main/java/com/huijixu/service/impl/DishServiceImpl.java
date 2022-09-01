package com.huijixu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huijixu.dto.DishDto;
import com.huijixu.mapper.DishMapper;
import com.huijixu.pojo.Dish;
import com.huijixu.pojo.DishFlavor;
import com.huijixu.service.DishFlavorService;
import com.huijixu.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 保存菜品修改内容
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateDishAndFlavor( DishDto dishDto) {
        this.updateById(dishDto);

        //delete dish_flavor from dish_flavors where dish_id = dishDto.id;
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //insert dish_flavor into dish_flavors which dish_flavors_id = dishDto.id ;
        List<DishFlavor> flavors = dishDto.getFlavors();
        //insert dish_flavor_id into each flavor
        flavors =
                flavors.stream().map((item) -> {
                    item.setDishId(dishDto.getId());
                    return item;
                }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 修改菜品回显页面的查询两张表
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithDishFlavors(Long id) {
        //查询dish
        Dish dish = this.getById(id);       //getById本类的接口IService方法

        DishDto dishDto = new DishDto();
        //最终是用DishDto封装json返回 ，所以要拷贝该数据到DishDto
        BeanUtils.copyProperties(dish, dishDto);

        //查询dish_flavors表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        //将一个菜品包含的一串口味的List set到dishdto中
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 多张表操作
     * 此处和启动类加入事务注解！！！
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveDishAndFlavor(DishDto dishDto) {

        //保存dish表
        //this。继承自其接口DishService-->IService.save()
        this.save(dishDto);   //保存Pojo用save

        /*
        下面为DishFlavor.pojo.dishId 赋值，多表事务需要靠这个外键联系
        也就是取出Flavor集合，去除每个flavor，为每个flavor.dishId 赋值
         */
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long dishId = dishDto.getId();


        flavors =
                flavors.stream().map((item) -> {
                    item.setDishId(dishId);
                    return item;
                }).collect(Collectors.toList());


        //保存dish_flavor表
        dishFlavorService.saveBatch(flavors); //保存集合用Batch

    }
}
