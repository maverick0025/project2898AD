package com.experiment.dsa1.gcalendar;

import com.experiment.dsa1.gmail.GmailServiceAndBuild;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
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
public class GoogleCalendar implements GoogleCalendarInterface{
    public Calendar calendarService = null;
    @Autowired
    private GmailServiceAndBuild gmailServiceAndBuild;

    public GoogleCalendar() {
    }

    @Override
    public void processCalendarEvents() throws IOException, MessagingException, GeneralSecurityException {

        CalendarList calendarList = calendarService.calendarList().list().setPageToken(null).execute();
        List<CalendarListEntry> items = calendarList.getItems();
        String signedInUserEmail = items.getFirst().getId();
        //calendarId is same as signedInUserEmail
        System.out.println("Signed In by: "+ signedInUserEmail);
        long currentTimeValue;
        long itemTimeValue;
        String pageToken = null;
        do{
            Events events = calendarService.events().list("primary").setPageToken(pageToken).setTimeMin(new DateTime(new Date())).execute();
            List<Event> eventList = events.getItems();
            if(!eventList.isEmpty()){
                for(Event event: eventList){
                    if(event.size() > 10){
                        String eventId = event.getId(); //event ID
                        //perform Events: get method using calendarId and EventID to know more about this event (we get actual start time)
                        //only then filter the events
                        DateTime start = event.getStart().getDateTime();
                        if(start == null){
                            start = event.getStart().getDate();
                        }else{
                            DateTime current = new DateTime(new Date());
                            currentTimeValue = current.getValue();
                            itemTimeValue= start.getValue();
                            boolean isRecurrent = event.getRecurrence() != null;
                            if(currentTimeValue != 0 && itemTimeValue != 0 && (itemTimeValue >= currentTimeValue || isRecurrent)){
                                System.out.println("summary: " + event.getSummary() + ", Attendees: "+ event.getAttendees());
                                if(event.getAttendees() != null && !Objects.equals(event.getSummary(), "Code Green meet")){
                                    gmailServiceAndBuild.buildAndSendEmail(event.getSummary(),
                                            event.getAttendees(),
                                            signedInUserEmail,
                                            event.getHtmlLink());
                                }
                            }
                        }
                    }
                }
            }else{
                System.out.println("No upcoming events");
            }
            pageToken = events.getNextPageToken();
        }while(pageToken != null);
    }

    @Override
    public void GCalendarService(String accessToken) throws IOException, GeneralSecurityException{

        long initial = (timeAtWhichAccessTokenGenerated.getTime() + accessTokenExpiration)*1000;
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, new Date(initial)));

        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credentials);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        calendarService = new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), httpRequestInitializer)
                .setApplicationName("project2898ad")
                .build();
    }
}
