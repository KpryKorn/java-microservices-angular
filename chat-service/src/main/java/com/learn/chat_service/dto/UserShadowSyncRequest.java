package com.learn.chat_service.dto;

import java.util.UUID;

public record UserShadowSyncRequest(
        UUID keycloakUserId,
        String username,
        String email) {
}
