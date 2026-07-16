package com.learn.chat_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.chat_service.entity.TicketMessage;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, UUID> {
}
