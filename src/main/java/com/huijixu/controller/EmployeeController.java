package com.huijixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huijixu.pojo.Employee;
import com.huijixu.service.EmployeeService;
import com.huijixu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author HuiJiXu
 * @address QingDao China
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;


    /**
     * 管理员修改员工界面员工信息回显
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(
            @PathVariable
            Long id
    ){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有此员工");
    }

    /**
     * 员工权限修改 &  信息编辑
     */
    @PutMapping
    public  R<String> update(
            HttpServletRequest request,
            @RequestBody
                    Employee employee
    ){
        //检查前端数据封装是否规格
        log.info(employee.toString());

        //线程信息
        Long thread = Thread.currentThread().getId();
        log.info("Controller 线程id:{}",thread);

        employeeService.updateById(employee);//因为前端给封装好的json数据就只有id和status，所以这里就省事了
        return R.success("修改成功！");
    }


    /**
     * 登录后的自动分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        log.info("page= {},pageSize= {}, name= {}", page, pageSize, name);

        //分页构造器  ( 查几条)
        Page pageConstructed = new Page(page, pageSize);

        //条件构造器  ( 按照什么查)
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        包装条件
        employeeLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//      排序条件 ，按照添加时间排序逆序
        employeeLambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //  查询  (用什么工具查)
        employeeService.page(pageConstructed,employeeLambdaQueryWrapper);  //分页构造器 & 查询条件

        return  R.success(pageConstructed);
    }


    @PostMapping("/login")
    public R<Employee> login(
            HttpServletRequest request,   //将http请求封装到request对象中
            @RequestBody    //将前端json对象封装为javaBean
                    Employee employee
    ) {
        //拿到前端密码并加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据前端用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);  // 查询数据库得到里面的emp


        //权限验证
        if (emp == null) {
            return R.error("登录失败,无此雇员！");
        }
        //不能用！=
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误！");
        }
        //检查该emp权限
        if (emp.getStatus() == 0) {
            return R.error("当前雇员权限已冻结！");
        }

        //登录成功，将员工id存入session并返回登录成功的结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(
            HttpServletRequest request
    ) {
        //将用户信息从session域中移除
        request.getSession().removeAttribute("employee");
        return R.success("成功登出!");
    }


    /**
     * 新增员工信息
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> saveEmployee(
            HttpServletRequest request,
            @RequestBody Employee employee
    ) {
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }
}
