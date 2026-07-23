package com.learn.chat_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.chat_service.dto.CreateTicketRequest;
import com.learn.chat_service.dto.TicketResponse;
import com.learn.chat_service.dto.UserShadowSyncRequest;
import com.learn.chat_service.entity.Ticket;
import com.learn.chat_service.entity.UserShadow;
import com.learn.chat_service.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserShadowService userShadowService;

    @Transactional
    public TicketResponse createTicket(Jwt jwt, CreateTicketRequest request) {
        UserShadowSyncRequest syncRequest = buildSyncRequestFromJwt(jwt);
        UserShadow userShadow = userShadowService.syncFromKeycloak(syncRequest);

        Ticket ticket = Ticket.builder().userShadow(userShadow).subject(request.subject()).motif(request.motif())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getCurrentUserTickets(UUID keycloakUserId) {
        return ticketRepository.findByUserShadowIdOrderByCreatedAtDesc(keycloakUserId).stream().map(this::mapToResponse)
                .toList();
    }

    private UserShadowSyncRequest buildSyncRequestFromJwt(Jwt jwt) {
        UUID keycloakUserId = UUID.fromString(jwt.getSubject());
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        return new UserShadowSyncRequest(keycloakUserId, username, email);
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
