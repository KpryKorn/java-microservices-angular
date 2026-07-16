package com.learn.user_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.user_service.entity.CommunicationPref;

public interface CommunicationPrefRepository extends JpaRepository<CommunicationPref, UUID> {
}
