package com.huy.pdoc.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health-check")
@CrossOrigin("*")
public class HealthCheckController {
    @GetMapping
    public String getHealthCheck(){
        return "HELLO WORLD";
    }
}
