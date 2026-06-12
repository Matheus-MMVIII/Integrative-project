package com.pi.model;

import java.io.IOException;

public class Manager {
    public Manager(String email, String password) throws IOException {
        Login login = new Login(email, password);
        if (!login.getType().equals("MANAGER"))
            throw new IllegalArgumentException("Invalid type. ");
    }
}
