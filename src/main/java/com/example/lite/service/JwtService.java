package com.example.lite.service;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.*;

public class JwtService {

    // Función que genera un token
    public static Function<String, String> generateToken(SecretKey secretKey, long expirationTime) {
        return (username) -> Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, HS256)
                .compact();
    }

    // Función que valida un token
    public static Function<String, Boolean> validateToken(SecretKey secretKey) {
        return token -> {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;  // Token válido
        };
    }

    // Función para extraer el username del token
    public static Function<String, String> extractUsername(SecretKey secretKey) {
        return (token) -> Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

