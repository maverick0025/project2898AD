package com.experiment.dsa1.gcalendar;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public interface GoogleCalendarInterface {

    public void processCalendarEvents() throws Exception;

    public void GCalendarService(String accessToken) throws Exception;

}
