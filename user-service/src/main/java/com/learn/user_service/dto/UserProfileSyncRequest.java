package com.learn.user_service.dto;

import java.util.UUID;

public record UserProfileSyncRequest(
        UUID keycloakUserId,
        String firstName,
        String lastName) {
}
