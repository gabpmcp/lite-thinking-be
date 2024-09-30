package com.example.lite.controller;

import com.example.lite.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

import static com.example.lite.service.JwtService.generateToken;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final Function<String, String> generateToken;
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(@Qualifier("generateTokenFn") Function<String, String> generateToken, ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.generateToken = generateToken;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody LoginRequest loginRequest) {
        return userDetailsService.findByUsername(loginRequest.username())
                .filter(user -> passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .map(user -> {
                    String token = generateToken.apply(user.getUsername());
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(401).body(Map.of("token", "Invalid credentials"))));
    }
}

record LoginRequest(String username, String password) {}
