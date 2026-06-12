package com.pi;

import com.pi.model.Login;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        new Login("admin@email.com", "123456");
    }
}