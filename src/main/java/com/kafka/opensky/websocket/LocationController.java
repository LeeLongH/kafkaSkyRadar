package com.kafka.opensky.websocket;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LocationController {
    private double myLat;
    private double myLng;

    @RequestMapping("/location")
    public void receiveLocation(@RequestParam double lat, @RequestParam double lng){
        this.myLat = lat;
        this.myLng = lng;
        System.out.println("BE user location: " + lat + ", " + lng);
    }
    public double getUserLat() { return this.myLat; }
    public double getUserLng() { return this.myLng; }
}
