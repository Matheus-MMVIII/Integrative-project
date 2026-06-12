package com.pi.model;

import java.io.IOException;

public class Administrator {
    public Administrator(String email, String password) throws IOException {
        Login login = new Login(email, password);
        if (!login.getType().equals("ADMIN"))
            throw new IllegalArgumentException("Invalid type. ");
    }
}
