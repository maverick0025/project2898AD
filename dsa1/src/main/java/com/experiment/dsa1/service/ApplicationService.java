package com.experiment.dsa1.service;

import com.experiment.dsa1.authenticationandauthorization.AccessTokenAndRefreshToken;
import com.experiment.dsa1.authenticationandauthorization.AuthorizationCode;
import com.experiment.dsa1.authenticationandauthorization.SharedVariables;
import com.experiment.dsa1.gcalendar.GoogleCalendarInterface;
import com.experiment.dsa1.gmail.GmailServiceAndBuild;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class ApplicationService {

    @Autowired
    private AuthorizationCode authorizationCode;
    @Autowired
    private AccessTokenAndRefreshToken accessTokenAndRefreshToken;
    @Autowired
    private GoogleCalendarInterface googleCalendarInterface;
    @Autowired
    private GmailServiceAndBuild gmailServiceAndBuild;
    @Autowired
    private SharedVariables sharedVariables;

    public ApplicationService() {
    }

    public void handleServices() throws Exception {

        String authCode = authorizationCode.getAuthorizationCode();

        accessTokenAndRefreshToken.getAccessAndRefreshTokens(authCode);

        googleCalendarInterface.GCalendarService(sharedVariables.getAccessTokenShareable());
        googleCalendarInterface.processCalendarEvents();

    }


}
