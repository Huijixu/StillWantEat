package com.huijixu.filter;

import com.alibaba.fastjson.JSON;
import com.huijixu.utils.BaseContext;
import com.huijixu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HuiJiXu
 * @address QingDao China
 */

@Slf4j
                /*  过滤器名称                    过滤地址样式    */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    /**
     * 与过滤器配合使用的 Request路径匹配 工具类
     * 支持通配符
     */
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        //向下转型
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}",request.getRequestURI());//输出日志

        //线程信息
        Long thread = Thread.currentThread().getId();
        log.info("Filter 线程id:{}",thread);

        //获取本次Request URI
        String requestURI = request.getRequestURI();

        //指定放行的请求白名单
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",  //因为Login在此目录下 ，所以要格外做个白名单
                "/front/**",
                /*
                添加前端登录验证请求地址
                 */
                "/user/login",
                "/user/sendMsg"

        };

        boolean ifNecessary = ifNecessary(requestURI, urls);
        //判断是否处理
        if ( ! ifNecessary){
            filterChain.doFilter(request,response);
            return;
        }

        //判断  管理端用户 登录状态(session会话时效内) ，若已经登录过直接放行 否返回登录界面
        if (request.getSession().getAttribute("employee") != null){   //已经登录过（比如说是返回页面并没有退出）

            /*
            当用户为登录状态时
            将其用户的id设置到ThreadLocal中，鉴于其良好的线程隔离性，为后面id在不同类的不同方法之间传递提供可能
             */
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //判断  前端用户  登录状态(session会话时效内)
        if (request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        /*
            这里由于前端登录页面绑定了一个外部js文件 ，
            里面的判断条件是写死的String字符串 ，所以这里看起来挺别扭
         */
        response.getWriter().write(JSON.toJSONString(R.error("NOT LOGIN")));
        return;

    }

    /**
     * 判断是否有必要进行拦截
     * @return
     */
    private  boolean  ifNecessary(String  requestURI,String[] URLS){
        for (String url :
                URLS) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            //在白名单中
            if (match) return  false;
        }
        return true;
    }
}
