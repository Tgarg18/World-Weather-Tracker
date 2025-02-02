package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtConstant {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    public static String staticSecretKey;
    public static final String JWT_HEADER = "Authorization";

    @jakarta.annotation.PostConstruct
    public void init() {
        staticSecretKey = SECRET_KEY;
    }

    public static String getStaticSecretKey() {
        return staticSecretKey;
    }
}
