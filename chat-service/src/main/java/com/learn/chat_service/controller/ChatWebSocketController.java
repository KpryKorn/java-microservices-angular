package com.learn.chat_service.controller;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.learn.chat_service.dto.TicketMessageResponse;
import com.learn.chat_service.dto.TicketMessageSendRequest;
import com.learn.chat_service.service.TicketMessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final TicketMessageService ticketMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/tickets/{ticketId}/messages")
    public void sendMessage(@DestinationVariable UUID ticketId, TicketMessageSendRequest request) {
        TicketMessageResponse response = ticketMessageService.createMessage(ticketId, request);
        messagingTemplate.convertAndSend("/topic/tickets/" + ticketId, response);
    }
}