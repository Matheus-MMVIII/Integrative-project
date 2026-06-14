package com.pi.service;

import com.pi.model.User;

import java.time.Duration;
import java.time.LocalDateTime;

public class Session {
    private static final Duration TIMEOUT = Duration.ofMinutes(15);
    private final User user;
    private LocalDateTime lastActivity;
    private boolean closed;

    public Session(User user) {
        this.user = user;
        this.lastActivity = LocalDateTime.now();
    }

    public User getUser() {
        validate();
        lastActivity = LocalDateTime.now();
        return user;
    }

    public boolean isExpired() {
        return closed || Duration.between(lastActivity, LocalDateTime.now()).compareTo(TIMEOUT) > 0;
    }

    public void close() {
        closed = true;
    }

    private void validate() {
        if (isExpired()) {
            closed = true;
            throw new IllegalStateException("Sessao expirada. Faca login novamente.");
        }
    }
}
