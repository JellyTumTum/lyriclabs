package com.project.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000",
                        "http://" + SecurityConfig.CURRENT_IP + ":8080",
                        "http://" + SecurityConfig.CURRENT_IP,
                        SecurityConfig.CURRENT_IP,
                        "https://lyriclabs.co.uk",
                        "https://cst.dev", "https://cst.dev/lyriclabs")

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Origin",
                        "Sec-WebSocket-Version",
                        "Sec-WebSocket-Extensions",
                        "Sec-WebSocket-Key",
                        "Authorization",
                        "Content-Type") // added a few more common headers just in case, better safe than sorry.
                .allowCredentials(true)
                .exposedHeaders("Authorization");

    }
}