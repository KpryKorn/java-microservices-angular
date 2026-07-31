package com.learn.chat_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.chat_service.dto.TicketMessageResponse;
import com.learn.chat_service.service.TicketMessageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat/tickets")
public class TicketMessageController {

    private final TicketMessageService ticketMessageService;

    @GetMapping("/{ticketId}/messages")
    public List<TicketMessageResponse> getMessages(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID ticketId) {
        return ticketMessageService.getMessagesByTicketId(ticketId);
    }
}
