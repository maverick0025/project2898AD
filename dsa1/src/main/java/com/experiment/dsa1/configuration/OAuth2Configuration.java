package com.experiment.dsa1.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("oauth2.google")
public class OAuth2Configuration {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private int authRedirectUriPort;
    private String authorizationEndPoint;
    private String accessAndRefreshTokensRequestUrl;
    private String refreshTokenRequestUrl;

    public OAuth2Configuration() {
    }

    public OAuth2Configuration(String clientId, String clientSecret, String redirectUri, int authRedirectUriPort, String authorizationEndPoint, String accessAndRefreshTokensRequestUrl, String refreshTokenRequestUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authRedirectUriPort = authRedirectUriPort;
        this.authorizationEndPoint = authorizationEndPoint;
        this.accessAndRefreshTokensRequestUrl = accessAndRefreshTokensRequestUrl;
        this.refreshTokenRequestUrl = refreshTokenRequestUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public int getAuthRedirectUriPort() {
        return authRedirectUriPort;
    }

    public void setAuthRedirectUriPort(int authRedirectUriPort) {
        this.authRedirectUriPort = authRedirectUriPort;
    }

    public String getAuthorizationEndPoint() {
        return authorizationEndPoint;
    }

    public void setAuthorizationEndPoint(String authorizationEndPoint) {
        this.authorizationEndPoint = authorizationEndPoint;
    }

    public String getAccessAndRefreshTokensRequestUrl() {
        return accessAndRefreshTokensRequestUrl;
    }

    public void setAccessAndRefreshTokensRequestUrl(String accessAndRefreshTokensRequestUrl) {
        this.accessAndRefreshTokensRequestUrl = accessAndRefreshTokensRequestUrl;
    }

    public String getRefreshTokenRequestUrl() {
        return refreshTokenRequestUrl;
    }

    public void setRefreshTokenRequestUrl(String refreshTokenRequestUrl) {
        this.refreshTokenRequestUrl = refreshTokenRequestUrl;
    }
}
