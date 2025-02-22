package com.experiment.dsa1.authenticationandauthorization;

import com.experiment.dsa1.configuration.OAuth2Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RefreshAccessToken {

    @Autowired
    private OAuth2Configuration oAuth2Configuration;
    @Autowired
    private SharedVariables sharedVariables;

    public void getRefreshedAccessToken(String refreshToken){
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("client_id", oAuth2Configuration.getClientId());
            params.put("client_secret", oAuth2Configuration.getClientSecret());
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

//            URL url = new URL(oAuth2Configuration.getRefreshTokenRequestUrl());
            URL url = new URI(oAuth2Configuration.getRefreshTokenRequestUrl()).toURL();
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

            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("old access token " + sharedVariables.getAccessTokenShareable() );

            String aToken = json.getString("access_token");
            sharedVariables.setAccessTokenShareable(aToken);
            long accessTokenExp = (long)json.getInt("expires_in");
            sharedVariables.setAccessTokenExpirationShareable(accessTokenExp);

            System.out.println("refreshed access token " + sharedVariables.getAccessTokenShareable());
            System.out.println("------------------------------------------------------------------------------------");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
