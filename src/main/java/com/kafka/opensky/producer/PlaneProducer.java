package com.kafka.opensky.producer;

import com.kafka.opensky.model.Plane;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PlaneProducer  {

    private final KafkaTemplate<String, Plane> kafkaTemplate;
    private final String topic =  "planes";
    public PlaneProducer(KafkaTemplate<String, Plane> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPlane(Plane plane) {
        kafkaTemplate.send(topic, plane.getIcao24(), plane);
        System.out.println("Sent plane " + plane.getIcao24());
    }
    public void sendPlanes(List<Plane> planes) {
        planes.forEach(this::sendPlane);
    }

}