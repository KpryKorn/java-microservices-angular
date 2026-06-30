package com.learn.api_gateway.security;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Configuration
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtReactiveAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            Claims claims = jwtService.validateAndGetClaims(token);
            String username = claims.getSubject();
            return Mono.just(new UsernamePasswordAuthenticationToken(username, null, List.of()));
        } catch (JwtException e) {
            return Mono.error(new BadCredentialsException("Invalid JWT"));
        }
    }
}
