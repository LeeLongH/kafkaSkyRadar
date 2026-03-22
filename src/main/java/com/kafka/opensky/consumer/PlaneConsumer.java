package com.kafka.opensky.consumer;

import com.kafka.opensky.model.Plane;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlaneConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public PlaneConsumer(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Getter
    private final Map<String, Plane> latestPlanes = new ConcurrentHashMap<>();

    @KafkaListener(topics="planes", groupId = "opensky-group")
    public void consumePlane(ConsumerRecord<String, Plane> record) {
        Plane plane = record.value();
        System.out.println("Consuming plane: " + plane.getCallsign() + " from " + plane.getOriginCountry() + ", on height: " + plane.getBaroAltitude());
        simpMessagingTemplate.convertAndSend("/topic/planes", plane);
    }
}
