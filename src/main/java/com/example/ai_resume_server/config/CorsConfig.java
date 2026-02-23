package com.example.ai_resume_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedMethods("GET","POST","PUT","DELETE")
                .allowedOrigins("http://localhost:5173",
                        "https://resume-intelligence-service.netlify.app",
                        "https://ris.vktech.live",
                        "http://localhost:5173",
                        "https://ris.vkstore.site"
                        )
                .allowedHeaders("*");
    }
}
