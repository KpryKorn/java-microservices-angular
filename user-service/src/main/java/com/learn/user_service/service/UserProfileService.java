package com.learn.user_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.user_service.dto.UserProfileSyncRequest;
import com.learn.user_service.entity.UserProfile;
import com.learn.user_service.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    /**
     * Crée ou met à jour le profil utilisateur à partir des informations Keycloak.
     * Appelé par l'api-gateway juste après une connexion réussie.
     */
    @Transactional
    public UserProfile syncFromKeycloak(UserProfileSyncRequest request) {
        UserProfile userProfile = userProfileRepository.findByKeycloakUserId(request.keycloakUserId())
                .orElseGet(() -> UserProfile.builder()
                        .keycloakUserId(request.keycloakUserId())
                        .accountActive(true)
                        .createdAt(LocalDateTime.now())
                        .build());

        userProfile.setFirstName(request.firstName());
        userProfile.setLastName(request.lastName());

        return userProfileRepository.save(userProfile);
    }
}
