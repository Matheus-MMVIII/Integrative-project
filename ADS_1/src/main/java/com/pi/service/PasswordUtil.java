package com.pi.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("A senha deve possuir ao menos 6 caracteres.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Nao foi possivel proteger a senha.", exception);
        }
    }
}
