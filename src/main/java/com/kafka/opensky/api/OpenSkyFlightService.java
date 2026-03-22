package com.kafka.opensky.api;

import com.kafka.opensky.model.OpenSkyResponse;
import com.kafka.opensky.model.Plane;
import com.kafka.opensky.producer.PlaneProducer;
import com.kafka.opensky.websocket.LocationBoundingBox;
import com.kafka.opensky.websocket.LocationController;
import com.kafka.opensky.websocket.VisibilityPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class OpenSkyFlightService {

    private final OpenSkyAuthService authService;
    private final PlaneProducer producer;
    private final RestTemplate restTemplate = new RestTemplate();

    private final LocationBoundingBox boundingBox;

    private final LocationController locationController;

    private final WeatherstackApi weatherstackApi;
    private final VisibilityPublisher visibilityPublisher;

    public OpenSkyFlightService(OpenSkyAuthService authService, PlaneProducer producer, LocationBoundingBox boundingBox, LocationController locationController, WeatherstackApi weatherstackApi, VisibilityPublisher visibilityPublisher) {
        this.authService = authService;
        this.producer = producer;
        this.boundingBox = boundingBox;
        this.locationController = locationController;
        this.weatherstackApi = weatherstackApi;
        this.visibilityPublisher = visibilityPublisher;
    }


    // Spring injects value from YAML
    @Value("${opensky.api-url}")
    private String apiUrl;

    @Scheduled(fixedRate = 10000)
    public void fetchFlights(){

        String token = authService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        double lat = locationController.getUserLat();
        double lng = locationController.getUserLng();

        if (lat == 0.0 && lng == 0.0) {
            System.out.println("Location not set yet. Skipping fetch.");
            return;
        }

        double visibilityDistance = weatherstackApi.getVisibility(lat, lng);

        String boxUrl = boundingBox.buildUrl(lat, lng, visibilityDistance);
        //System.out.println("User lat/lng: " + lat + ", " + lng);
        //System.out.println("Visibility distance (km): " + visibilityDistance);
        //System.out.println("Generated BOX URL: " + boxUrl);

        ResponseEntity<Map> skyResponse = restTemplate.exchange(
                boxUrl,
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map body = skyResponse.getBody();
        OpenSkyResponse openSkyResponse = new OpenSkyResponse();
        openSkyResponse.setTime(((Number) body.get("time")).longValue());

        List<List<Object>> states = (List<List<Object>>) body.get("states");
        List<Plane> planes = new ArrayList<>();

        System.out.println("Number of states returned: " + (states != null ? states.size() : 0));

        if (states == null) {
            System.out.println("No flight states returned from OpenSky.");
            return;
        }

        for (List<Object> s : states) {
            Plane plane = new Plane();
            plane.setIcao24((String) s.get(0));
            plane.setCallsign((String) s.get(1));
            plane.setOriginCountry((String) s.get(2));
            plane.setTimePosition(s.get(3) != null ? ((Number) s.get(3)).longValue() : null);
            plane.setLastContact(s.get(4) != null ? ((Number) s.get(4)).longValue() : null);
            plane.setLongitude(s.get(5) != null ? ((Number) s.get(5)).doubleValue() : null);
            plane.setLatitude(s.get(6) != null ? ((Number) s.get(6)).doubleValue() : null);
            plane.setBaroAltitude(s.get(7) != null ? ((Number) s.get(7)).doubleValue() : null);
            plane.setOnGround((Boolean) s.get(8));
            plane.setVelocity(s.get(9) != null ? ((Number) s.get(9)).doubleValue() : null);
            plane.setTrueTrack(s.get(10) != null ? ((Number) s.get(10)).doubleValue() : null);
            plane.setVerticalRate(s.get(11) != null ? ((Number) s.get(11)).doubleValue() : null);
            plane.setSensors((List<Integer>) s.get(12));
            plane.setGeoAltitude(s.get(13) != null ? ((Number) s.get(13)).doubleValue() : null);
            plane.setSquawk(s.get(14) != null ? s.get(14).toString() : null);
            plane.setSpi((Boolean) s.get(15));
            plane.setPositionSource(s.get(16) != null ? ((Number) s.get(16)).intValue() : null);

            planes.add(plane);
        }

        visibilityPublisher.sendVisibility(visibilityDistance);
        openSkyResponse.setPlanes(planes);
        producer.sendPlanes(planes);
    }
}
