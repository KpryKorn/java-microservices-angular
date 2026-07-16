package com.learn.user_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.user_service.dto.UserProfileSyncRequest;
import com.learn.user_service.entity.UserProfile;
import com.learn.user_service.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/sync")
    public UserProfile sync(@RequestBody UserProfileSyncRequest request) {
        return userProfileService.syncFromKeycloak(request);
    }
}
