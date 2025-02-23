package com.example.linknote.config;

import com.example.linknote.filter.CustomFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    /**
     * 手动注册过滤器并设置顺序
     */
    @Bean
    public FilterRegistrationBean<CustomFilter> customFilterRegistration(CustomFilter customFilter) {
        FilterRegistrationBean<CustomFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(customFilter);
        registration.addUrlPatterns("/*"); // 过滤所有请求
        registration.setOrder(1); // 设置过滤器执行顺序
        return registration;
    }
}