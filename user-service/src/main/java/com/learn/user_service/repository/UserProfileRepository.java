package com.learn.user_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import main.java.com.learn.user_service.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
