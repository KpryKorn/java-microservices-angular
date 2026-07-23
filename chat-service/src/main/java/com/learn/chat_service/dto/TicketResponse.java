package com.learn.chat_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        UUID userId,
        String subject,
        String motif,
        String status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt) {

}
