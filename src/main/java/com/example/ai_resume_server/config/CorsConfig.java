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
                        "http://localhost:63342/Ai-Resume-Analyzer/ai-resume-server/static/index.html?_ijt=fca4s748v865209lfmb70rhs6v&_ij_reload=RELOAD_ON_SAVE",
                        "http://localhost:3000",
                        "https://ai-resume-analyzer-teal-three.vercel.app/"
                        )
                .allowedHeaders("*");
    }
}
