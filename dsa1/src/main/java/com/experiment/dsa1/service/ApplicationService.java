package com.experiment.dsa1.service;

import com.experiment.dsa1.authenticationandauthorization.AccessTokenAndRefreshToken;
import com.experiment.dsa1.authenticationandauthorization.AuthorizationCode;
import com.experiment.dsa1.gcalendar.GoogleCalendarInterface;
import com.experiment.dsa1.gmail.GmailServiceAndBuild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.experiment.dsa1.authenticationandauthorization.AccessTokenAndRefreshToken.accessToken;

@Service
public class ApplicationService {

    @Autowired
    private AuthorizationCode authorizationCode;
    @Autowired
    private AccessTokenAndRefreshToken accessTokenAndRefreshToken;
    @Autowired
    private static GoogleCalendarInterface googleCalendarInterface;
    @Autowired
    private GmailServiceAndBuild gmailServiceAndBuild;

    public ApplicationService() {
    }

    public void handleServices() throws Exception {

        String authCode = authorizationCode.getAuthorizationCode();
        accessTokenAndRefreshToken.getAccessAndRefreshTokens(authCode);
        googleCalendarInterface.GCalendarService(accessToken);
        System.out.println("handle Services accesstoken: "+ accessToken);
        googleCalendarInterface.processCalendarEvents();

    }


}
