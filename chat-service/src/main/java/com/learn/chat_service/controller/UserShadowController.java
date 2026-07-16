package com.learn.chat_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.chat_service.dto.UserShadowSyncRequest;
import com.learn.chat_service.entity.UserShadow;
import com.learn.chat_service.service.UserShadowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat/users")
@RequiredArgsConstructor
public class UserShadowController {

    private final UserShadowService userShadowService;

    @PostMapping("/sync")
    public UserShadow sync(@RequestBody UserShadowSyncRequest request) {
        return userShadowService.syncFromKeycloak(request);
    }
}
