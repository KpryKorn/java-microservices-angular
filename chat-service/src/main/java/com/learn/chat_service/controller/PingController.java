package com.learn.chat_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
