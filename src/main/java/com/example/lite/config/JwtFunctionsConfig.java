package com.example.lite.config;

import com.example.lite.service.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import io.vavr.control.Option;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

@Configuration
public class JwtFunctionsConfig {

    // Cargar el secreto desde una variable de entorno o usar un valor predeterminado seguro
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(System.getProperty("JWT_SECRET", "")
                    .getBytes(StandardCharsets.UTF_8)
    ));

    @Bean
    public Function<String, String> generateTokenFn() {
        // 1 hora de expiración
        long expirationTime = 3600000;
        return JwtService.generateToken(secretKey, expirationTime);
    }

    @Bean
    public Function<String, Boolean> validateTokenFn() {
        return JwtService.validateToken(secretKey);
    }

    @Bean
    public Function<String, String> extractUsernameFn() {
        return JwtService.extractUsername(secretKey);
    }
}
