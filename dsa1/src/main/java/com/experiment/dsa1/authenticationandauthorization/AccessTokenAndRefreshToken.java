package com.experiment.dsa1.authenticationandauthorization;

import com.experiment.dsa1.configuration.OAuth2Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class AccessTokenAndRefreshToken {

    @Autowired
    private OAuth2Configuration oAuth2Configuration;

    @Autowired
    private RefreshAccessToken refreshAccessToken;

    public static String accessToken = null;
    public static String refreshToken = null;
    public static Long accessTokenExpiration;
    public static Date timeAtWhichAccessTokenGenerated;
    public static long timeAtWhichAccessWillExpire;

    public void getAccessAndRefreshTokens(String authCode){

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("code", authCode);
        params.put("client_id", oAuth2Configuration.getClientId());
        params.put("client_secret", oAuth2Configuration.getClientSecret());
        params.put("redirect_uri", oAuth2Configuration.getRedirectUri());
        params.put("grant_type", "authorization_code");

        StringBuilder postData = new StringBuilder();

        for(Map.Entry<String, Object> param : params.entrySet()){

            if(!postData.isEmpty()){
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            if(Objects.equals(param.getKey(), "redirect_uri")){
                postData.append((String) param.getValue());
            }else{
                postData.append(URLEncoder.encode((String) param.getValue(), StandardCharsets.UTF_8));
            }
        }

        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        try {
            URL url = new URL(oAuth2Configuration.getAccessAndRefreshTokensRequestUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.getOutputStream().write(postDataBytes);

            if (connection.getResponseCode() >= 400) {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder buffer = new StringBuilder();
                    String line;
                    while ((line = reader2.readLine()) != null) {
                        buffer.append(line);
                    }
                    reader2.close();

                    // Process the error message
                    String errorMessage = buffer.toString();
                    System.out.println("Error message: " + errorMessage);
                }
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();

                for(String line = reader.readLine(); line!=null; line = reader.readLine()){
                    buffer.append(line);
                }
                JSONObject json = new JSONObject(buffer.toString());

                accessToken = json.getString("access_token");
                refreshToken = json.getString("refresh_token");
                accessTokenExpiration = (long)json.getInt("expires_in"); //in seconds
                timeAtWhichAccessTokenGenerated = new Date();

                timeAtWhichAccessWillExpire = timeAtWhichAccessTokenGenerated.getTime() + accessTokenExpiration*1000;

            }
        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }

    @Scheduled(cron = "0 */7 * * * *")
    public void checkIfAccessTokenExpired(){
        Date current = new Date();

        System.out.println("Running access token expiration cron and current time is: " + current);

        if(current.getTime() - timeAtWhichAccessTokenGenerated.getTime() >= accessTokenExpiration*1000){

            System.out.println("Access token has expired and current time is "+ (current.getTime() - timeAtWhichAccessTokenGenerated.getTime()));

            refreshAccessToken.getRefreshedAccessToken(refreshToken);
        }
    }
}
