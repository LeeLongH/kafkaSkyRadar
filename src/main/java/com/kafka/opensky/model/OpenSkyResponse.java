package com.kafka.opensky.model;

import lombok.Data;
import java.util.List;

@Data
public class OpenSkyResponse {
    private Long time;
    private List <Plane> planes;
}
