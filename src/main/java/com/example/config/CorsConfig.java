package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000/","http://localhost:3001/", "https://fe-fashions-shoes-admin.vercel.app/", "https://fe-fashions-shoes-user.vercel.app/")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
