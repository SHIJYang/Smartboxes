package org.example.boxes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 提供 PasswordEncoder Bean，解决 SecurityServiceImpl 报错问题
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 配置安全过滤链，解决接口被拦截问题
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (因为你是用 Token 机制，不需要 Session/Cookie)
                .csrf(AbstractHttpConfigurer::disable)
                // 允许跨域
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 配置请求权限
                .authorizeHttpRequests(auth -> auth
                        // 允许所有人访问登录和注册接口
                        .requestMatchers("/api/users/login", "/api/users", "/api/users/register").permitAll()
                        // 允许 Swagger 文档 (可选)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // 其他所有请求都需要认证（你可以暂时设为 permitAll() 方便调试，但生产环境要 authenticated()）
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // 3. 配置跨域 (CORS)，配合 Uniapp 开发
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有来源
        config.addAllowedHeader("*");        // 允许所有头
        config.addAllowedMethod("*");        // 允许所有方法 (GET, POST等)
        config.setAllowCredentials(true);    // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}