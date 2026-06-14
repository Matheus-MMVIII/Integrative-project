package com.pi.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Login {
    private final String EMAIL;
    private final String PASSWORD;
    private AccountsType type;

    public Login(String email, String password) throws IOException {
        if (!validateAccount(email, password, getValidAccounts()))
            throw new IllegalArgumentException("Invalid login. ");
        this.EMAIL = email;
        this.PASSWORD = password;
    }

    private boolean validateAccount(String email, String password, List<String> accounts) {

        for (int i = 1; i < accounts.size(); i++) {
            String[] data = accounts.get(i).split(",");
            if (data[0].equals(email) && data[1].equals(password)) {
                type = AccountsType.valueOf(data[2]);
                return true;
            }
        }
        return false;
    }

    private List<String> getValidAccounts() throws IOException {
        return Files.readAllLines(Path.of("ADS_1/data/Accounts.csv"));
    }

    public String getType() {
        return type.name();
    }

}

enum AccountsType {
    ADMIN, MANAGER, SELLER
}
