package com.huijixu;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author HuiJiXu
 * @address QingDao China
 */
public class test {
    @Test
    public void test(){
        String s = "111111.jpg";

        //获取新文件名
        String fileName = UUID.randomUUID().toString();
        //获取源文件的后缀名
        String substring = s.substring(s.lastIndexOf("."));
        //组建新文件名
        fileName = fileName + substring;
        System.out.println(fileName);
    }
}
