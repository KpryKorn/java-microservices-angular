package com.learn.chat_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.chat_service.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
