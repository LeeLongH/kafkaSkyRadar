package com.kafka.opensky.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class VisibilityPublisher {

    private final SimpMessagingTemplate template;

    public VisibilityPublisher(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendVisibility(double visibilityKm) {
        template.convertAndSend("/topic/visibility", visibilityKm);
    }
}