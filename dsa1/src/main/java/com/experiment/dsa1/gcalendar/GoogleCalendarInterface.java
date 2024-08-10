package com.experiment.dsa1.gcalendar;

public interface GoogleCalendarInterface {

    public default void processCalendarEvents() throws Exception{

    };

    public default void GCalendarService(String accessToken) throws Exception{

        return;
    };

}
