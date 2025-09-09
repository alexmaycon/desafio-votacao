package com.voting.system.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(true)
                .parameterName("version")
                .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .mediaType("v1", org.springframework.http.MediaType.APPLICATION_JSON)
                .mediaType("v2", org.springframework.http.MediaType.APPLICATION_JSON);
    }
}
