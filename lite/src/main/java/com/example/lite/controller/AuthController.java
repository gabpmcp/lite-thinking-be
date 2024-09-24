package com.example.lite.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest loginRequest) {
        return userDetailsService.findByUsername(loginRequest.email())
                .filter(user -> passwordEncoder.matches(loginRequest.password(), user.getPassword()))
                .map(user -> ResponseEntity.ok("Login Success"))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials")));
    }
}

record LoginRequest(String email, String password) {}