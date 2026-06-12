package com.pi.model;

import java.io.IOException;

public class Seller {

    public Seller(String email, String password) throws IOException {
        Login login = new Login(email, password);
        if (!login.getType().equals("SELLER"))
            throw new IllegalArgumentException("Invalid type. ");
    }
}
