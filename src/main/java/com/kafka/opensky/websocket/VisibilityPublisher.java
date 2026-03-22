package com.kafka.opensky.websocket;

import com.kafka.opensky.api.WeatherstackApi;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class VisibilityPublisher {

    private final SimpMessagingTemplate template;
    private final WeatherstackApi weatherstackApi;

    public VisibilityPublisher(SimpMessagingTemplate template, WeatherstackApi weatherstackApi) {
        this.template = template;
        this.weatherstackApi = weatherstackApi;
    }

    public void sendVisibility(double lat, double lng) {
        // fetch visibility once
        double visibilityKm = weatherstackApi.getVisibility(lat, lng);

        // send once to the browser
        template.convertAndSend("/topic/visibility", visibilityKm);
    }
}