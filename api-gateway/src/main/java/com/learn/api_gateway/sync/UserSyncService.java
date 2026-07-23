package com.learn.api_gateway.sync;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Propage les informations de l'utilisateur Keycloak vers les microservices qui
 * ont besoin d'une copie locale (chacun ayant sa propre base de données).
 * Appelé après une connexion OAuth2 réussie, en best-effort : un échec de
 * synchronisation
 * ne doit jamais empêcher l'utilisateur de se connecter.
 */
@Component
public class UserSyncService {

        private static final Logger log = LoggerFactory.getLogger(UserSyncService.class);

        private final WebClient webClient;
        private final String userServiceUrl;
        private final String chatServiceUrl;

        public UserSyncService(
                        @Value("${app.user-service-url}") String userServiceUrl,
                        @Value("${app.chat-service-url}") String chatServiceUrl) {
                this.webClient = WebClient.builder().build();
                this.userServiceUrl = userServiceUrl;
                this.chatServiceUrl = chatServiceUrl;
        }

        public Mono<Void> syncUser(OidcUser oidcUser, String accessToken) {
                UUID keycloakUserId = UUID.fromString(oidcUser.getSubject());

                Mono<Void> syncProfile = webClient.post()
                                .uri(userServiceUrl + "/api/user/profile/sync")
                                .headers(headers -> headers.setBearerAuth(accessToken))
                                .bodyValue(
                                                new UserProfileSyncRequest(keycloakUserId, oidcUser.getGivenName(),
                                                                oidcUser.getFamilyName()))
                                .retrieve()
                                .toBodilessEntity()
                                .doOnError(error -> log.error(
                                                "Échec de synchronisation du profil utilisateur (user-service)", error))
                                .onErrorResume(error -> Mono.empty())
                                .then();

                Mono<Void> syncShadow = webClient.post()
                                .uri(chatServiceUrl + "/api/chat/users/sync")
                                .headers(headers -> headers.setBearerAuth(accessToken))
                                .bodyValue(
                                                new UserShadowSyncRequest(keycloakUserId,
                                                                oidcUser.getPreferredUsername(), oidcUser.getEmail()))
                                .retrieve()
                                .toBodilessEntity()
                                .doOnError(error -> log.error(
                                                "Échec de synchronisation de l'utilisateur (chat-service)", error))
                                .onErrorResume(error -> Mono.empty())
                                .then();

                return Mono.when(syncProfile, syncShadow);
        }
}
