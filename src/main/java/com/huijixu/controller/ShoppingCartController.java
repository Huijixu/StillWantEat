package com.huijixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huijixu.pojo.ShoppingCart;
import com.huijixu.service.ShoppingCartService;
import com.huijixu.utils.BaseContext;
import com.huijixu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
@SuppressWarnings({"All"})
@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 购物车添加商品
     */
    @PostMapping("add")
    public R<ShoppingCart> add(
            @RequestBody
                    ShoppingCart shoppingCart
    ) {
        log.info("shopcart={}",shoppingCart);
        //查询当前登录用户id
        Long currentId = BaseContext.getCurrentId();

        //设置当前购物车id为当前登录用户id
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        //判断用户下单种类 并加入查询条件
        if (dishId != null) {   //下单的菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId() );
        }

        //查询当前添加的菜品/套餐是否已经存在于购物车中
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        //判断不是第一份则份数加1
        if ( one != null) {
/**
 *在 购物车中存在说明在数据库空也存在
 * 所以此时改的书数据库的数据
 */
            one.setNumber(one.getNumber() + 1 );
            shoppingCartService.updateById(one);    //更新操作！！！
        }else{
            /**
            不在购物车 也就不在数据库，所以时候先把前端封装的数据模型的部分属性修改接着在保存到数据库中
             */
           shoppingCart.setNumber(1);
           shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);  //添加操作！！
            //返回为了回显数据
            one = shoppingCart;

        }

        return R.success(one);
    }

    /**
     * 用户查看购物车
     */
    @GetMapping("/list")
    public  R<List<ShoppingCart>> show(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //展示顺序
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("已清空");
    }

}
