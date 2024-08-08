package com.experiment.dsa1.authenticationandauthorization;


import com.experiment.dsa1.Dsa1Application;
import com.experiment.dsa1.configuration.OAuth2Configuration;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;
import fi.iki.elonen.NanoHTTPD;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AuthorizationCode {

    @Autowired
    private OAuth2Configuration oAuth2Configuration;
    protected String authCode = null;

    public String getAuthorizationCode() throws IOException, URISyntaxException, InterruptedException {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(1);
        OAuth2Server server = new OAuth2Server(oAuth2Configuration.getAuthRedirectUriPort(), urlQueue);
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

        String authorizationUrl = String.format(
                "%s?scope=%s&access_type=%s&include_granted_scopes=%s&response_type=%s&state=%s&redirect_uri=%s&client_id=%s",
                oAuth2Configuration.getAuthorizationEndPoint(),
                CalendarScopes.CALENDAR + "&" + GmailScopes.GMAIL_COMPOSE,
//                "https://www.googleapis.com/auth/calendar&https://www.googleapis.com/auth/gmail.compose",
                "offline",
                "true",
                "code",
                "state_parameter_passthrough_value",
                oAuth2Configuration.getRedirectUri(),
                oAuth2Configuration.getClientId()
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
