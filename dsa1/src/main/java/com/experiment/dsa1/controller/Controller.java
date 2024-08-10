package com.experiment.dsa1.controller;

import com.experiment.dsa1.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/")
    public ResponseEntity<String> beginProcess() throws Exception {

        applicationService.handleServices();
        return new ResponseEntity(HttpStatusCode.valueOf(200));
    }

}
