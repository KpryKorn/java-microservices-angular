package com.learn.api_gateway.security;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoderFactory;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.learn.api_gateway.sync.UserSyncService;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final URI frontendUri;
    private final URI chatRedirectUri;
    private final String keycloakIssuerUri;
    private final UserSyncService userSyncService;

    public SecurityConfig(@Value("${app.frontend-url}") String frontendUrl,
            @Value("${app.keycloak-issuer-uri}") String keycloakIssuerUri,
            UserSyncService userSyncService) {
        this.frontendUri = URI.create(frontendUrl);
        this.chatRedirectUri = URI.create(frontendUrl + "/chat");
        this.keycloakIssuerUri = keycloakIssuerUri;
        this.userSyncService = userSyncService;
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
                        .pathMatchers("/api/chat/**", "/api/user/**", "/api/me", "/ws/**").authenticated()
                        .anyExchange().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                            return userSyncService.syncUser(oidcUser)
                                    .then(Mono.defer(() -> {
                                        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                                        webFilterExchange.getExchange().getResponse().getHeaders()
                                                .setLocation(chatRedirectUri);
                                        return webFilterExchange.getExchange().getResponse().setComplete();
                                    }));
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
            decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(keycloakIssuerUri));
            return decoder;
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUri.toString()));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(frontendUri);
        return logoutSuccessHandler;
    }
}
