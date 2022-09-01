package com.huijixu.config;

import com.huijixu.utils.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author HuiJiXu
 * @address QingDao China
 */

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 扩展mvc框架的消息转换器
     * @param converters 默认与配置的消息转换器集合
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //线程信息
        Long thread = Thread.currentThread().getId();
        log.info("WebMvcConfig 线程id:{}",thread);

        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);

    }
}
