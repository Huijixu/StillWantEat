package com.huijixu.dto;


import com.huijixu.pojo.Setmeal;
import com.huijixu.pojo.SetmealDish;
import lombok.Data;

import java.util.List;
/**
 * @author HuiJiXu
 * @address QingDao China
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
