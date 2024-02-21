package com.uniovi.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.InitBinder;

@Service
public class InsertSampleDataService {
    private final UserService userService;

    public InsertSampleDataService(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        userService.addNewUser("test@test.com", "Test", "TestS");
    }
}
