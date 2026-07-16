package com.learn.api_gateway.sync;

import java.util.UUID;

public record UserShadowSyncRequest(
        UUID keycloakUserId,
        String username,
        String email) {
}
