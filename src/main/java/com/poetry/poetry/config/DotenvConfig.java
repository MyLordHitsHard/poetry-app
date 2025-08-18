package com.poetry.poetry.config;


import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DotenvConfig {
    static {

        if (!"true".equals(System.getenv("RUNNING_IN_DOCKER"))) {
            Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
            dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        }


    }
}
