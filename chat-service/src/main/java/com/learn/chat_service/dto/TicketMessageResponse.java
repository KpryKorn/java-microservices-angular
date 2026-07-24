package com.learn.chat_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketMessageResponse(
                UUID id,
                UUID ticketId,
                UUID senderId,
                String senderUsername,
                String content,
                LocalDateTime sentAt) {
}