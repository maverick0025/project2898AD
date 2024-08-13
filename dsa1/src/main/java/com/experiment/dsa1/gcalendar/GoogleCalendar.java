package com.experiment.dsa1.gcalendar;

import com.experiment.dsa1.configuration.OAuth2Configuration;
import com.experiment.dsa1.gmail.GmailServiceAndBuild;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.experiment.dsa1.authenticationandauthorization.AccessTokenAndRefreshToken.accessTokenExpiration;
import static com.experiment.dsa1.authenticationandauthorization.AccessTokenAndRefreshToken.timeAtWhichAccessTokenGenerated;

@Component
public class GoogleCalendar implements GoogleCalendarInterface {
    public Calendar calendarService = null;
    @Autowired
    private GmailServiceAndBuild gmailServiceAndBuild;

    @Autowired
    private OAuth2Configuration oAuth2Configuration;

    @Override
    public void processCalendarEvents() throws IOException, MessagingException, GeneralSecurityException {

        CalendarList calendarList = calendarService.calendarList().list().setPageToken(null).execute();
        List<CalendarListEntry> items = calendarList.getItems();
        String signedInUserEmail = items.getFirst().getId();

        //calendarId is same as signedInUserEmail
        System.out.println("Signed In by: " + signedInUserEmail);

        String pageToken = null;
        do {
            Events events = calendarService.events().list("primary").setPageToken(pageToken).setTimeMin(new DateTime(new Date())).execute();
            List<Event> eventList = events.getItems();
            if (!eventList.isEmpty()) {
                for (Event event : eventList) {
                    if (event.size() > 10) {
                        if (!checkEventValidity(event)) {
                            continue;
                        }
//                        System.out.println("summary: " + event.getSummary() + ", Attendees: " + event.getAttendees() + ", start time value: " + event.getStart().getDateTime().getValue());
                        System.out.println("Yeah its a valid event");
                        if (event.getAttendees() != null && !Objects.equals(event.getSummary(), "Code Green meet")) {
                            gmailServiceAndBuild.buildAndSendEmail(event.getSummary(),
                                    event.getAttendees(),
                                    signedInUserEmail,
                                    event.getHtmlLink());
                        }
                    }
                }
            } else {
                System.out.println("No upcoming events");
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
    }

    @Override
    public void GCalendarService(String accessToken) throws IOException, GeneralSecurityException {

        long initial = (timeAtWhichAccessTokenGenerated.getTime() + accessTokenExpiration) * 1000;
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, new Date(initial)));

        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credentials);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        calendarService = new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), httpRequestInitializer)
                .setApplicationName("project2898ad")
                .build();
    }

    private boolean checkEventValidity(Event event) throws IOException {

        DateTime current = new DateTime(new Date());
        long currentTimeValue = current.getValue();

        int attendeesCount = event.getAttendees() != null ? event.getAttendees().size() : 0;

        if(currentTimeValue > event.getStart().getDateTime().getValue() || attendeesCount < 2){
            return false;
        }

        //check if the event is in the next one hour.
        long eventStart = event.getStart().getDateTime().getValue();
        long timeDiffInMinutes = (eventStart - currentTimeValue)/(60 * 1000); //60 for minutes and 1000 for milliseconds

        System.out.println("summary: " + event.getSummary() + " time difference to the current time in minutes: " + timeDiffInMinutes);
        return timeDiffInMinutes <= 120;

    }

}
