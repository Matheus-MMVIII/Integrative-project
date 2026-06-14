package com.pi.model;

import com.pi.repository.UserRepository;
import com.pi.service.AuthService;

import java.io.IOException;

public class Login {
    private final User user;

    public Login(String email, String password) throws IOException {
        user = new AuthService(new UserRepository()).login(email, password).getUser();
    }

    public String getType() {
        return user.getRole().name();
    }
}
