package com.nutriguide.dietplanner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String apiTest() {
        return "API Test: Working ✅";
    }

    @GetMapping("/api/health")
    public String health() {
        return "Server Health: Good 💚";
    }
}