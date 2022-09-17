package com.huijixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huijixu.dto.DishDto;
import com.huijixu.pojo.Category;
import com.huijixu.pojo.Dish;
import com.huijixu.pojo.DishFlavor;
import com.huijixu.service.CategoryService;
import com.huijixu.service.DishFlavorService;
import com.huijixu.service.DishService;
import com.huijixu.utils.R;
//import com.sun.org.apache.bcel.internal.generic.ATHROW;
//import jdk.nashorn.internal.ir.CallNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@RestController
@RequestMapping("/dish")
@Slf4j
@SuppressWarnings({"all"})

public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 单个菜品起售停售   0: 停售；1：起售
     */
    @PostMapping("/status/0{id}")
    public R<String> updateSaleMsg(
            @PathVariable
                    Long id
    ) {
        Dish dish = dishService.getById(id);
        Integer status = dish.getStatus();
        if (status == 1) {
            dish.setStatus(0);
        }
        if (status == 0) {
            dish.setStatus(1);
        }
        dishService.updateById(dish);

        return R.success("修改成功");
    }


    /**
     *  dish展示
     *  使用redis 缓存
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(DishDto dish   ) {

        /**
        构造动态key 从redis 中查询 数据，数据在redis 中设计为按照 菜品分类存贮
        所以key要做成自动拼接的dishId + status
        说白就是拼接一个url类型的get请求
         */

        List<DishDto> dishDtoList = null;

        //此key为redis中的索引key
         String key  = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

         /*
         1
         从redis中查询数据
          */
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        /*
         1.1
         查到直接返回
         */
        if (dishDtoList  != null ){
            return  R.success(dishDtoList);
        }

        /*
         2
         redis中查不到 进入数据库
          */
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        /**
         * 查询口味信息
         */
         dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

         /*
         2.1
        将数据库库中查到的数据存入redis
          */
        redisTemplate.opsForValue().set(key,dishDtoList,30, TimeUnit.MINUTES);
         return R.success(dishDtoList);
    }

    /**
     * 菜品修改表单提交提交更新
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateDishAndFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 修改菜品页面信息回显
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishDtoInfo(
            @PathVariable
                    Long id
    ) {
        DishDto dishDto = dishService.getByIdWithDishFlavors(id);
        return R.success(dishDto);
    }


    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> addDish(
            @RequestBody
                    DishDto dishDto
    ) {
        log.info("保存新菜品数据中。。。");
        log.info(dishDto.toString());
//            dishService.save(dishDto);
        dishService.saveDishAndFlavor(dishDto);

        return R.success("新增菜品成功！");
    }


    @GetMapping("/page")
    public R<Page> pageR(int page, int pageSize, String name) {

        Page<Dish> dishInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //搜索过滤条件
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        //排序规则
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort);

        dishService.page(dishInfo, dishLambdaQueryWrapper);
        /*
        下面要将菜品管理页面的每条"菜品分类"展示
        根据前端的编写：“菜品分类”的展示是通过"菜品名称"在Category查到的
        根据以上分析，该页面所含有的json数据跨一个pojo，需要DTO
         */

        /**
         * 步骤分析：
         * 1. 新建dishDtoInfo分页查询插件
         * 2. 将dishInfo 中除了records(Page中村塾对象信息的集合)之外的值赋值给dishDtoInfo
         * 3. 赋值records，将Dish的属性逐个取出，stream流的方式逐个写入到DishDto对象的实例中
         */
        Page<DishDto> dishDtoInfo = new Page<>();

        //对象信息copy
        BeanUtils.copyProperties(dishInfo, dishDtoInfo, "records"); //第三个参数指定不赋值的属性

        //两个要交换数据的集合
        List<Dish> dishList = dishInfo.getRecords();

        List<DishDto> dishDtoList

                = dishList.stream().map((item) -> {  //这里的item就是Dish
            DishDto dishDto = new DishDto();

            //copy Dish属性值 to DishDto里
            BeanUtils.copyProperties(item, dishDto);

            //dishDto.categoryName属性赋值
            //根据records的里面”菜品名称“的id在categoryService中查询其所属”菜品分类“
            Long categoryId = item.getCategoryId();

            if (categoryId.equals("")) {
                throw new RuntimeException("”菜品管理“页面查询不到数据库对应“菜品分类”id！");
            }
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());

            //最后和为dishDtoList的每个元素
            return dishDto;

        }).collect(Collectors.toList());

        //将dishDtoList  赋值 给dishDtoInfo
        dishDtoInfo.setRecords(dishDtoList);


        return R.success(dishDtoInfo);
    }

}
