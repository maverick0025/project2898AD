package com.experiment.dsa1.authenticationandauthorization;


import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SharedVariables {
    private String accessTokenShareable;
    private Long accessTokenExpirationShareable;
    private Date timeAtWhichAccessTokenGeneratedShareable;
    public Long getAccessTokenExpirationShareable() {
        return accessTokenExpirationShareable;
    }

    public Date getTimeAtWhichAccessTokenGeneratedShareable() {
        return timeAtWhichAccessTokenGeneratedShareable;
    }

    public void setTimeAtWhichAccessTokenGeneratedShareable(Date timeAtWhichAccessTokenGenerated) {
        this.timeAtWhichAccessTokenGeneratedShareable = timeAtWhichAccessTokenGenerated;
    }

    public void setAccessTokenExpirationShareable(Long accessTokenExpirationShareable) {
        this.accessTokenExpirationShareable = accessTokenExpirationShareable;
    }

    public String getAccessTokenShareable() {
        return accessTokenShareable;
    }

    public void setAccessTokenShareable(String accessTokenShareable) {
        this.accessTokenShareable = accessTokenShareable;
    }
}
