package com.example.Kirby_mini_2nd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://192.168.5.24:5173") // Vue의 URL (포트 번호 확인 필요)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*") // 허용할 헤더
                .allowCredentials(true);
    }
}

