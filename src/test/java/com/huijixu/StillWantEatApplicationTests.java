package com.huijixu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class StillWantEatApplicationTests {

    @Test
    void contextLoads() {
        String s = DigestUtils.md5DigestAsHex("123456".getBytes());
        System.out.println(s);
    }

}
