package com.learn.api_gateway.sync;

import java.util.UUID;

public record UserProfileSyncRequest(
        UUID keycloakUserId,
        String firstName,
        String lastName) {
}
