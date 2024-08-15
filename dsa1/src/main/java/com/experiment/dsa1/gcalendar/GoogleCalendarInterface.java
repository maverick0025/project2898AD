package com.experiment.dsa1.gcalendar;

import org.springframework.stereotype.Component;

@Component
public interface GoogleCalendarInterface {

    void processCalendarEvents() throws Exception;

    void GCalendarService(String accessToken) throws Exception;

}
