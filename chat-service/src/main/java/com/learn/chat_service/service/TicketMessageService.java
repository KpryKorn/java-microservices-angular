package com.learn.chat_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.chat_service.dto.TicketMessageResponse;
import com.learn.chat_service.dto.TicketMessageSendRequest;
import com.learn.chat_service.entity.Ticket;
import com.learn.chat_service.entity.TicketMessage;
import com.learn.chat_service.repository.TicketMessageRepository;
import com.learn.chat_service.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketMessageService {

        private final TicketRepository ticketRepository;
        private final TicketMessageRepository ticketMessageRepository;

        @Transactional
        public TicketMessageResponse createMessage(UUID ticketId, UUID senderId, String senderUsername,
                        TicketMessageSendRequest request) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable: " + ticketId));

                TicketMessage message = TicketMessage.builder()
                                .ticket(ticket)
                                .senderId(senderId)
                                .content(request.content())
                                .sentAt(LocalDateTime.now())
                                .build();

                TicketMessage savedMessage = ticketMessageRepository.save(message);

                return new TicketMessageResponse(
                                savedMessage.getId(),
                                ticketId,
                                savedMessage.getSenderId(),
                                senderUsername,
                                savedMessage.getContent(),
                                savedMessage.getSentAt());
        }
}