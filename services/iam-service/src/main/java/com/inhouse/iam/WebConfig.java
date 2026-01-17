package com.inhouse.iam;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置，包含 CORS 与登录校验。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TokenService tokenService;

    public WebConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(tokenService))
                .addPathPatterns("/iam/**")
                .excludePathPatterns("/auth/**");
    }
}
