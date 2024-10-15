package org.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")  // Updated route here
public class DocumentsController {

    @GetMapping("/test")
    public String testEndpoint() {
        return "Dummy response from backend!";
    }
}
