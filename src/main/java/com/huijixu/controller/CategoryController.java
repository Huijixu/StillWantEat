package com.huijixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huijixu.pojo.Category;
import com.huijixu.service.CategoryService;
import com.huijixu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Slf4j
@RestController
@RequestMapping("/category")
@SuppressWarnings({"all"})
public class CategoryController {

    @Autowired
    private CategoryService categoryService ;

    /**
     * 根据条件查询分类数据
     * 显示在添加菜品--菜品分类--下拉框里
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> last(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType() != null ,Category::getType,category.getType());
        //两个排序条件保证当表中有一样的category.sort时，依然可以排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }

    @PutMapping
    public R<String> updateById(
            @RequestBody   //不加这个注释后端就拿不到数据啊千万别忘了！！！
            Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }



    /**
     * 删除菜品分类
     */

    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return  R.success("成功删除");
    }

    /**
     * 菜品分类分页查询
     */
    @GetMapping("/page")
    public R<Page> pageR(int page ,int pageSize){
        //查几条
        Page<Category> categoryInfo = new Page<>(page,pageSize);
        //按照什么查
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //正序排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //用什么工具查
        categoryService.page(categoryInfo,lambdaQueryWrapper);

        return R.success(categoryInfo);
    }

    /**
     * 新增菜品或套餐分类
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(
            @RequestBody Category category
            ){
        categoryService.save(category);
        return R.success("新增分类成功");
    }


}
