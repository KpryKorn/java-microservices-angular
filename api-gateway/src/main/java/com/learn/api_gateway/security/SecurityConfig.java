package com.learn.api_gateway.security;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoderFactory;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public SecurityConfig() {
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, denied) -> {
                            if (exchange.getRequest().getPath().value().startsWith("/api/")) {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }
                            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getResponse().getHeaders()
                                    .setLocation(URI.create("/oauth2/authorization/keycloak"));
                            return exchange.getResponse().setComplete();
                        }))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/chat/**").authenticated()
                        .anyExchange().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders()
                                    .setLocation(URI.create("http://localhost:4200"));
                            return webFilterExchange.getExchange().getResponse().setComplete();
                        }))
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .build();
    }

    @Bean
    public ReactiveJwtDecoderFactory<ClientRegistration> jwtDecoderFactory() {
        return clientRegistration -> {
            NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                    .withJwkSetUri(clientRegistration.getProviderDetails().getJwkSetUri())
                    .build();
            decoder.setJwtValidator(new JwtTimestampValidator());
            return decoder;
        };
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("http://localhost:4200"));
        return logoutSuccessHandler;
    }
}
