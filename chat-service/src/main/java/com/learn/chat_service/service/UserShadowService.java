package com.learn.chat_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.chat_service.dto.UserShadowSyncRequest;
import com.learn.chat_service.entity.UserShadow;
import com.learn.chat_service.repository.UserShadowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserShadowService {

    private final UserShadowRepository userShadowRepository;

    /**
     * Crée ou met à jour la copie locale (shadow) de l'utilisateur à partir des
     * informations Keycloak. Appelé par l'api-gateway juste après une connexion
     * réussie.
     */
    @Transactional
    public UserShadow syncFromKeycloak(UserShadowSyncRequest request) {
        UserShadow userShadow = userShadowRepository.findById(request.keycloakUserId())
                .orElseGet(() -> UserShadow.builder()
                        .id(request.keycloakUserId())
                        .build());

        userShadow.setUsername(request.username());
        userShadow.setEmail(request.email());
        userShadow.setSyncedAt(LocalDateTime.now());

        return userShadowRepository.save(userShadow);
    }
}
