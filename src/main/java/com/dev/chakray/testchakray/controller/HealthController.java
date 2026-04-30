package com.dev.chakray.testchakray.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HealthController {
    
    @GetMapping("/healthcheck")
    public String hello() {
        return "Application is running =)";
    }

}
