package com.kafka.opensky.websocket;

import jakarta.annotation.PostConstruct;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/ping")
    public void ping(){
        // empty handler
    }
    @PostConstruct
    public void init() {
        System.out.println("WebSocketController loaded");
    }
}
