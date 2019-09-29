package com.github.mtesmct.rieau.api.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    private final AppProperties properties;

    @Autowired
    public CORSConfig(AppProperties properties){
        this.properties = properties;
    }
 
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(this.properties.getCorsAllowedOrigins());
    }
}