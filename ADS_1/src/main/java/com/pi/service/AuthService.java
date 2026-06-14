package com.pi.service;

import com.pi.model.Role;
import com.pi.model.User;
import com.pi.repository.UserRepository;

import java.io.IOException;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Session login(String email, String password) throws IOException {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPasswordHash().equals(PasswordUtil.hash(password))) {
            throw new IllegalArgumentException("E-mail ou senha invalidos.");
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException("Usuario inativo.");
        }
        return new Session(user);
    }

    public static void requireRole(User user, Role... allowedRoles) {
        for (Role role : allowedRoles) {
            if (user.getRole() == role) {
                return;
            }
        }
        throw new SecurityException("Seu perfil nao possui acesso a esta funcionalidade.");
    }
}
