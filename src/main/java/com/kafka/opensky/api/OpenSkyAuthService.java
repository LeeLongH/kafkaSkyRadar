package com.kafka.opensky.api;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.util.Map;
@Service
public class OpenSkyAuthService {
    @Value("${opensky.client-id}")
    private String clientId;
    @Value("${opensky.client-secret}")
    private String clientSecret;

    // for HTTP requests
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body =
                    "grant_type=client_credentials" +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try{
            // send POST request to OpenSKy auth server
            Map response = restTemplate.postForObject(
                    "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token",
                    entity,
                    Map.class
            );

            return response.get("access_token").toString();

        }catch(Exception e){
            System.out.print("\nOpen Sky not responsive: " + e.getMessage());
            System.exit(-10);
        }
        return null;
    }
}
