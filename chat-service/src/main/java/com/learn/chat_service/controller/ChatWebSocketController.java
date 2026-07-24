package com.learn.chat_service.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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
    public void sendMessage(@DestinationVariable UUID ticketId, @Payload TicketMessageSendRequest request,
            SimpMessageHeaderAccessor headerAccessor) {

        Map<String, Object> attrs = headerAccessor.getSessionAttributes();

        UUID senderId = UUID.fromString((String) attrs.get("userId"));
        String senderUsername = (String) attrs.get("username");

        TicketMessageResponse response = ticketMessageService.createMessage(ticketId, senderId, senderUsername,
                request);
        messagingTemplate.convertAndSend("/topic/tickets/" + ticketId, response);
    }
}