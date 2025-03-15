package com.example.linknote.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 记录请求信息
        logger.info("Request URL: {}", httpRequest.getRequestURL());
        logger.info("HTTP Method: {}", httpRequest.getMethod());

        try {
            // 关键：必须调用 chain.doFilter 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 记录响应信息
            logger.info("Response Status: {}", httpResponse.getStatus());
        }
    }
}