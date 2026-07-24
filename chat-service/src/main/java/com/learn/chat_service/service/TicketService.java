package com.learn.chat_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.learn.chat_service.dto.CreateTicketRequest;
import com.learn.chat_service.dto.TicketResponse;
import com.learn.chat_service.entity.Ticket;
import com.learn.chat_service.entity.UserShadow;
import com.learn.chat_service.repository.TicketRepository;
import com.learn.chat_service.repository.UserShadowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserShadowRepository userShadowRepository;

    @Transactional
    public TicketResponse createTicket(Jwt jwt, CreateTicketRequest request) {
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());

        UserShadow userShadow = userShadowRepository.findById(keycloakUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT,
                        "Profil utilisateur introuvable, reconnectez-vous"));

        Ticket ticket = Ticket.builder()
                .userShadow(userShadow)
                .subject(request.subject())
                .motif(request.motif())
                .build();

        return mapToResponse(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getCurrentUserTickets(Jwt jwt) {
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());
        return ticketRepository
                .findByUserShadowIdOrderByCreatedAtDesc(keycloakUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getUserShadow().getId(),
                ticket.getSubject(),
                ticket.getMotif(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getResolvedAt());
    }
}