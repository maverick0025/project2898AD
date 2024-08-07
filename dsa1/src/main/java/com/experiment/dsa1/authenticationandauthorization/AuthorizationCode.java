package com.experiment.dsa1.authenticationandauthorization;


import com.experiment.dsa1.Dsa1Application;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;
import fi.iki.elonen.NanoHTTPD;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AuthorizationCode {
    @Value("${oauth2.google.client_id}")
    private final String clientId;

    @Value("${oauth2.google.client_secret}")
    private final String clientSecret;

    @Value("${oauth2.google.redirect_uri}")
    private final String redirectUri;

    @Value("${oauth2.google.auth_redirect_uri_port}")
    private final Integer authRedirectUriPort;

    @Value("${oauth2.google.authorization_end_point}")
    private final String authorizationEndPoint;

    protected String authCode = null;

    public AuthorizationCode(String clientId,
                             String clientSecret,
                             String redirectUri,
                             Integer authRedirectUriPort,
                             String authorizationEndPoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authRedirectUriPort = authRedirectUriPort;
        this.authorizationEndPoint = authorizationEndPoint;
    }

    public String getAuthorizationCode() throws IOException, URISyntaxException, InterruptedException {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(1);
        OAuth2Server server = new OAuth2Server(authRedirectUriPort, urlQueue);
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

        String authorizationUrl = String.format(
                "%s?scope=%s&access_type=%s&include_granted_scopes=%s&response_type=%s&state=%s&redirect_uri=%s&client_id=%s",
                authorizationEndPoint,
                CalendarScopes.CALENDAR + "&" + GmailScopes.GMAIL_COMPOSE,
//                "https://www.googleapis.com/auth/calendar&https://www.googleapis.com/auth/gmail.compose",
                "offline",
                "true",
                "code",
                "state_parameter_passthrough_value",
                redirectUri,
                clientId
        );

        String os = System.getProperty("os.name").toLowerCase();

        if (os.startsWith("win")) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(authorizationUrl));
            }
        } else if (os.contains("nux")) {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("/usr/bin/firefox -new-window" + authorizationUrl);
        } else if (os.contains("mac")) {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("open " + authorizationUrl);
        } else {
            throw new RuntimeException("unable to detect os");
        }

        String capturedUrl = urlQueue.take();
        String[] temp = capturedUrl.split("&");

        authCode = temp[1].split("=")[1];

        return authCode;
    }

}
