package com.example.lite.interceptors;

import com.example.lite.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class JwtAuthenticationFilter implements WebFilter {

    private final Function<String, Boolean> validateToken;
    private final Function<String, String> extractUsername;

    public JwtAuthenticationFilter(Function<String, Boolean> validateToken, Function<String, String> extractUsername) {
        this.validateToken = validateToken;
        this.extractUsername = extractUsername;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Obtener el encabezado Authorization
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Verificar si existe el encabezado y empieza con "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);  // Extraer el token quitando el prefijo "Bearer "

            // Validar el token y extraer el nombre de usuario
            if (validateToken.apply(token)) {
                String username = extractUsername.apply(token);

                // Crear la autenticación basada en el token JWT
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, null);  // No tenemos detalles del usuario, pero podemos usar el username

                // Continuar la cadena de filtros y establecer el contexto de seguridad con la autenticación
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            }
        }

        // Si no hay token o no es válido, continuar sin modificar el contexto de seguridad
        return chain.filter(exchange);
    }
}

