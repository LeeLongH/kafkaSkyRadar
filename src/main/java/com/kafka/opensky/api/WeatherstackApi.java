package com.kafka.opensky.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherstackApi {
    @Value("${weatherstack.api-url}")
    private String apiUrl;

    @Value("${weatherstack.access_key}")
    private String accessKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // get visibility in km from Weatherstack
    public double getVisibility(double latitude, double longitude) {
        try {
            // Construct URL with latitude and longitude
            String url = apiUrl + "?access_key=" + accessKey + "&query=" + latitude + "," + longitude;

            // GET request
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // visibility is inside "current"
            Map<String, Object> current = (Map<String, Object>) response.get("current");
            if (current != null && current.get("visibility") != null) {
                return ((Number) current.get("visibility")).doubleValue();
            }

            System.out.println("Visibility not found in response");
            return 20.0;

        } catch (Exception e) {
            System.out.println("Weatherstack not responsive: " + e.getMessage());
            return 20.0;
        }
    }
}
