package com.learn.chat_service.dto;

import java.util.UUID;

public record TicketMessageSendRequest(UUID senderId, String content) {
}