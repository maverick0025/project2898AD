package com.experiment.dsa1.authenticationandauthorization;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class RefreshAccessToken {


    @Value("${oauth2.google.client_id}")
    private final String clientId;
    @Value("${oauth2.google.client_secret}")
    private final String clientSecret;

    @Value("${oauth2.google.refresh_token_request_end_point}")
    private final String refreshTokenUrl;

    public RefreshAccessToken(String clientId, String clientSecret, String refreshTokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshTokenUrl = refreshTokenUrl;
    }

    public String getRefreshedAccessToken(String refreshToken){
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("refresh_token", refreshToken);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (!postData.isEmpty()) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
            }
            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

            URL url = new URL(refreshTokenUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.getOutputStream().write(postDataBytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            System.out.println(json);

            String accessToken = json.getString("access_token");

            return accessToken;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
