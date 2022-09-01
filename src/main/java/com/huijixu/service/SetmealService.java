package com.huijixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huijixu.dto.SetmealDto;
import com.huijixu.pojo.Setmeal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
     void saveWithDish(SetmealDto setmealDto) ;


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    void removeWithDish(List<Long> ids);

}
