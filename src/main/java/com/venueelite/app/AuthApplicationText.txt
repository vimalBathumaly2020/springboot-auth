package com.venueelite.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(AuthApplication.class, args);
    }

    private static void loadEnv() {
        try {
            java.io.File file = new java.io.File(".env");
            if (!file.exists()) return;

            java.util.Properties props = new java.util.Properties();
            props.load(new java.io.FileInputStream(file));

            props.forEach((key, value) ->
                    System.setProperty(key.toString(), value.toString())
            );
        } catch (Exception e) {
            System.out.println("Could not load .env file: " + e.getMessage());
        }
    }
}
