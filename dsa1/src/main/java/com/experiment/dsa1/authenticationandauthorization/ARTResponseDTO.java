package com.experiment.dsa1.authenticationandauthorization;

import java.util.Date;

public class ARTResponseDTO {

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiration;
    private Date timeAtWhichAccessTokenGenerated;
    public ARTResponseDTO() {
    }

    public ARTResponseDTO(String accessToken, String refreshToken, Long accessTokenExpiration, Date timeAtWhichAccessTokenGenerated){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiration = accessTokenExpiration;
        this.timeAtWhichAccessTokenGenerated = timeAtWhichAccessTokenGenerated;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Long accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public Date getTimeAtWhichAccessTokenGenerated() {
        return timeAtWhichAccessTokenGenerated;
    }

    public void setTimeAtWhichAccessTokenGenerated(Date timeAtWhichAccessTokenGenerated) {
        this.timeAtWhichAccessTokenGenerated = timeAtWhichAccessTokenGenerated;
    }
}
