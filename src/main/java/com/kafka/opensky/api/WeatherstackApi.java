package com.kafka.opensky.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherstackApi {
    @Value("${weatherstack.api-url}")
    private String apiUrl;

    @Value("${weatherstack.access_key}")
    private String accessKey;

    private Double cachedVisibility = null;
    private long lastFetchTime = 0;

    private static final long CACHE_DURATION = 60 * 60 * 1000; // 1 hour

    private final RestTemplate restTemplate = new RestTemplate();

    public double getVisibility(double latitude, double longitude) {

        long now = System.currentTimeMillis();

        if (cachedVisibility != null && (now - lastFetchTime) < CACHE_DURATION) {
            //System.out.println("Using cached visibility: " + cachedVisibility);
            return cachedVisibility;
        }

        double visibility;

        try {
            // Construct URL with latitude and longitude
            String url = apiUrl + "?access_key=" + accessKey + "&query=" + latitude + "," + longitude;

            // GET request
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // visibility is inside "current"
            Map<String, Object> current = (Map<String, Object>) response.get("current");
            if (current != null && current.get("visibility") != null) {
                visibility = ((Number) current.get("visibility")).doubleValue();
            }else {

                System.out.println("Visibility not found in response");
                visibility = 30.0;
            }

        } catch (Exception e) {
            System.out.println("Weatherstack api error: " + e.getMessage());
            visibility = 30;
        }

        cachedVisibility = visibility;
        lastFetchTime = now;

        return visibility;
    }
}
