package com.example.lite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                                .pathMatchers("/orchestrator").authenticated() // Requiere autenticación
                                .anyExchange().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(login -> login.disable()
                        .httpBasic(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
            .password("{noop}admin123")
            .roles("ADMIN")
            .build();

        UserDetails user = User.withUsername("user")
            .password("{noop}user123")
            .roles("USER")
            .build();

        return new MapReactiveUserDetailsService(admin, user);
    }
}