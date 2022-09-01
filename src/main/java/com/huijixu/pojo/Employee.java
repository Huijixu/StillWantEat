package com.huijixu.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HuiJiXu
 * @address QingDao China
 */



@Data
@NoArgsConstructor
public class Employee implements Serializable {
    private  static  final long  serialVersionUID = 1L;
    /**
     * 这个bug找了不知道多少天！！！！
     * 1.后面在用到employee实体时候，在session域中去除empId时是强转为Long类型的
     * 导致了和从数据库取出来的类型不一，抛出”类型不匹配异常”
     * 2.id值是数据库雪花算法生成的，用Integer类型接受Id值极易导致数据溢出，抛出NumberOutOfRange异常
     *
     */
    private Long id;

    private String  name;
    private String  username;
    private String password;
    private String phone;
    private String sex;
    //身份证号
    private String idNumber;
    //雇员权限
    private Integer status;


    /*
        自动填充策略：
        1. 实体类指定要自定填充的字段
        2. utils.MetaObjectHandler 做填充动作
     */
    @TableField(fill =  FieldFill.INSERT)   //自动填充策略：插入（update）时填充
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //插入和更新时填充
    private LocalDateTime updateTime;

    /*
    创建新雇员的表单也直接在employee中写
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;



}
