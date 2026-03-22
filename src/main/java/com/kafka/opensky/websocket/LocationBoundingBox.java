package com.kafka.opensky.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocationBoundingBox {

    @Value("${opensky.api-url}")
    private String openskyApiUrl;

    public String buildUrl(double latitude, double longitude, double visibilityDistanceKm) {

        final double EARTH_RADIUS_KM = 6371.0;

        if (visibilityDistanceKm <= 0) {
            visibilityDistanceKm = 1;
        }

        // Convert to radians
        double latRad = Math.toRadians(latitude);
        double lonRad = Math.toRadians(longitude);

        double angularDistance = visibilityDistanceKm / EARTH_RADIUS_KM;

        // Bounding box in radians
        double minLat = latRad - angularDistance;
        double maxLat = latRad + angularDistance;

        double minLon;
        double maxLon;

        if (minLat > Math.toRadians(-90) && maxLat < Math.toRadians(90)) {

            double deltaLon = Math.asin(Math.sin(angularDistance) / Math.cos(latRad));

            minLon = lonRad - deltaLon;
            maxLon = lonRad + deltaLon;

        } else {
            // Near poles → longitude is irrelevant
            minLat = Math.max(minLat, Math.toRadians(-90));
            maxLat = Math.min(maxLat, Math.toRadians(90));
            minLon = Math.toRadians(-180);
            maxLon = Math.toRadians(180);
        }

        // Convert back to degrees
        double lamin = Math.toDegrees(minLat);
        double lamax = Math.toDegrees(maxLat);
        double lomin = Math.toDegrees(minLon);
        double lomax = Math.toDegrees(maxLon);

        // Normalize longitude
        lomin = (lomin + 540) % 360 - 180;
        lomax = (lomax + 540) % 360 - 180;

        // 🔍 DEBUG LOGS
        System.out.println("====== BOUNDING BOX DEBUG ======");
        System.out.println("Center: " + latitude + ", " + longitude);
        System.out.println("Radius (km): " + visibilityDistanceKm);
        System.out.println("Lat range: " + lamin + " → " + lamax);
        System.out.println("Lon range: " + lomin + " → " + lomax);

        String url = String.format(Locale.US,
                "%s?lamin=%.6f&lamax=%.6f&lomin=%.6f&lomax=%.6f",
                openskyApiUrl,
                lamin, lamax, lomin, lomax
        );

        System.out.println("Generated URL: " + url);

        return url;
    }
}
