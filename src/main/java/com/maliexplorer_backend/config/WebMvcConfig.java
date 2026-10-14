package com.maliexplorer_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
<<<<<<< HEAD
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
=======
        Path uploadDir = Paths.get("uploads/images").toAbsolutePath().normalize();
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:" + uploadDir.toString() + "/");
>>>>>>> 81c82d1b135213e0cb34fa3b6388b507d7f1b919
    }
}

