package com.learn.chat_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learn.chat_service.dto.CreateTicketRequest;
import com.learn.chat_service.dto.TicketResponse;
import com.learn.chat_service.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(jwt, request);
    }

    @GetMapping
    public List<TicketResponse> getCurrentUserTickets(@AuthenticationPrincipal Jwt jwt) {
        return ticketService.getCurrentUserTickets(jwt);
    }
}
