package com.example.Kirby_mini_2nd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // 정적 리소스의 경로를 직접 지정해주어서 캐싱된 것을 사용하지 않는다.
        // 이미지 업로드 이후 서버에 요청시 못 찾던 문제 해결
        registry.addResourceHandler("/**")
                .addResourceLocations("file:src/main/resources/static/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(60 * 60 * 24 * 365);
    }

}
