package com.kafka.opensky.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plane implements Serializable {

    private String icao24;             // 0
    private String callsign;           // 1
    private String originCountry;      // 2
    private Long timePosition;         // 3
    private Long lastContact;          // 4
    private Double longitude;          // 5
    private Double latitude;           // 6
    private Double baroAltitude;       // 7
    private Boolean onGround;          // 8
    private Double velocity;           // 9
    private Double trueTrack;          // 10
    private Double verticalRate;       // 11
    private List<Integer> sensors;     // 12
    private Double geoAltitude;        // 13
    private String squawk;             // 14
    private Boolean spi;               // 15
    private Integer positionSource;    // 16
    private Integer category;          // 17
}
