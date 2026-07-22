package com.learn.chat_service.dto;

public record CreateTicketRequest(
        String subject,
        String motif) {
}
