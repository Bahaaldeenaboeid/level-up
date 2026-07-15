package com.yourstore.core.security;

public class SecurityConstants {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 86400000;

    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/products/**",
            "/api/categories/**",
            "/api/cities/**"
    };

    public static final String[] ADMIN_URLS = {
            "/api/admin/**"
    };

    private SecurityConstants() {}
}