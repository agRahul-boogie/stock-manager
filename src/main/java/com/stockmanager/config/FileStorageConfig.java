package com.stockmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.File;

@Configuration
public class FileStorageConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.output.dir}")
    private String outputDir;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public boolean createDirectories() {
        boolean uploadDirCreated = new File(uploadDir).mkdirs();
        boolean outputDirCreated = new File(outputDir).mkdirs();
        return uploadDirCreated || outputDirCreated;
    }
}