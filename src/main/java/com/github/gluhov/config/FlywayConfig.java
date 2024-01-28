package com.github.gluhov.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FlywayConfig {
    private final Properties properties;

    public FlywayConfig(String path) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new RuntimeException("Flyway properties file not found at " + path);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading Flyway properties from " + path, e);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}