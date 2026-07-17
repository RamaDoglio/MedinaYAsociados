package com.medina.asocDev.Medina.Asociados.security;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    private CorsConfig createCorsConfigWithOrigins(String origins) throws Exception {
        CorsConfig corsConfig = new CorsConfig();
        Field field = CorsConfig.class.getDeclaredField("corsOrigins");
        field.setAccessible(true);
        field.set(corsConfig, origins);
        return corsConfig;
    }

    @Test
    void webMvcConfigurer_beanIsCreated() throws Exception {
        CorsConfig corsConfig = createCorsConfigWithOrigins("http://localhost:5173");
        WebMvcConfigurer configurer = corsConfig.webMvcConfigurer();
        assertNotNull(configurer);
    }

    @Test
    void webMvcConfigurer_configuresCorsMappings() throws Exception {
        CorsConfig corsConfig = createCorsConfigWithOrigins("http://localhost:5173");
        WebMvcConfigurer configurer = corsConfig.webMvcConfigurer();

        CorsRegistry registry = new CorsRegistry();
        assertDoesNotThrow(() -> configurer.addCorsMappings(registry));
    }
}
