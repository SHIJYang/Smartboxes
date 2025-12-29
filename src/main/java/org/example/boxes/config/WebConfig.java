package org.example.boxes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 负责全局跨域设置，允许前端(Uniapp/Vue)跨域访问接口
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 使用 allowedOriginPatterns 代替 allowedOrigins，
                // 这样才能配合 allowCredentials(true) 使用
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // 允许携带凭证 (如 Cookies 或 Authorization 头)
                .allowCredentials(true)
                // 设置预检请求(OPTIONS)的缓存时间为1小时
                .maxAge(3600);
    }
}