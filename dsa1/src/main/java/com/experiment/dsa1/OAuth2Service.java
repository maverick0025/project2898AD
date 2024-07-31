package com.experiment.dsa1;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OAuth2Service {


    private final RestTemplate restTemplate = new RestTemplate();
    private final String clientId = "998890755657-o4bsgukkf7u186ronht27hrv1nt5sg7r.apps.googleusercontent.com";
    private final String clientSecret = "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";
    private final String tokenURL = "https://oauth2.googleapis.com/token";
    private final String redirectUri = "http://localhost:8080";

    public String getAccessToken(String code){

        HttpHeaders headers = new HttpHeaders();
        Map<String, String> body = new HashMap<>();

        body.put("code", code);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("redirect_uri", redirectUri);
        body.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenURL, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}
