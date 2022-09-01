package com.huijixu.dto;

import com.huijixu.pojo.Dish;
import com.huijixu.pojo.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
/**
 * @author HuiJiXu
 * @address QingDao China
 */


@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
