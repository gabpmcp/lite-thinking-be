package com.example.lite.config;

import com.example.lite.interceptors.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.function.Function;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final Function<String, Boolean> validateToken;
    private final Function<String, String> extractUsername;

    // Inyectar las funciones de JwtConfig sin ciclo de dependencias
    public SecurityConfig(@Qualifier("validateTokenFn") Function<String, Boolean> validateToken,
                          @Qualifier("extractUsernameFn") Function<String, String> extractUsername) {
        this.validateToken = validateToken;
        this.extractUsername = extractUsername;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt para codificar las contraseñas
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.cors(Customizer.withDefaults())  // Nueva forma de habilitar CORS
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/orchestrator").authenticated() // Requiere autenticación
                        .anyExchange().permitAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(new JwtAuthenticationFilter(validateToken, extractUsername), AUTHENTICATION)
                .build();
    }

    // Configuración CORS global
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);  // Permitir cookies y credenciales
        config.addAllowedOrigin("http://localhost:3000");  // Permitir el frontend en localhost:3000
        config.addAllowedHeader("*");  // Permitir todos los encabezados
        config.addAllowedMethod("*");  // Permitir todos los métodos HTTP

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Aplicar a todas las rutas
        return source;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123")) // Codificar la contraseña
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user123")) // Codificar la contraseña
                .roles("USER")
                .build();

        return new MapReactiveUserDetailsService(admin, user);
    }
}
