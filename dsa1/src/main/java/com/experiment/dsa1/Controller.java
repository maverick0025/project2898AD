package com.experiment.dsa1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private OAuth2Service oAuth2Service;

    @GetMapping("/gettoken")
    public String getToken(@RequestParam String code){

        return oAuth2Service.getAccessToken(code);
    }
}
