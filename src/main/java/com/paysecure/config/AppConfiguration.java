package com.paysecure.config;

import org.springframework.context.annotation.Configuration;

/**
 * FINDING 1 (Part A) — REMEDIATED.
 * Secrets are now sourced from environment variables, never committed to source.
 * DB_PASSWORD and ENCRYPTION_KEY have no fallback default on purpose: if the
 * environment variable isn't set, these will be null and the app will fail
 * loudly at startup rather than silently falling back to a hardcoded value.
 */
@Configuration
public class AppConfiguration {
    public static final String DB_URL =
            System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/paysecure");
    public static final String DB_USER =
            System.getenv().getOrDefault("DB_USER", "paysecure_admin");
    public static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    public static final String ENCRYPTION_KEY = System.getenv("ENCRYPTION_KEY");
}
