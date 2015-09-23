package fi.vincit.mutrproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.vincit.mutrproject.service.TestService;

@RestController
public class TestController {
    @Autowired
    private TestService service;

    @RequestMapping("/")
    public String home() {
        return "Hello World!" + service.getUsername();
    }

    @RequestMapping("/admin")
    public String admin() {
        return "Hello Admin!" + service.getAdmin();
    }
}
