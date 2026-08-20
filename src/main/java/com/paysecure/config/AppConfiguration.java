package com.paysecure.config;

import org.springframework.context.annotation.Configuration;

/**
 * FINDING 1 (Part A): Hardcoded secrets in source.
 * "The repository is private, therefore the values are considered safe." - dev team assumption under test.
 */
@Configuration
public class AppConfiguration {
    public static final String DB_URL =
            "jdbc:mysql://localhost:3306/paysecure";
    public static final String DB_USER = "paysecure_admin";
    public static final String DB_PASSWORD = "Admin@123456";
    public static final String ENCRYPTION_KEY = "PaySecure2026Key";
}
