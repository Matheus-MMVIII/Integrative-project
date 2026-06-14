package com.pi.repository;

import com.pi.model.Role;
import com.pi.model.User;
import com.pi.utils.CsvUtil;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String HEADER = "id,name,email,passwordHash,role,active";
    private final Path path;

    public UserRepository() throws IOException {
        path = DataFiles.data("Accounts.csv");
        DataFiles.ensure(path, HEADER);
    }

    public synchronized User create(User user) throws IOException {
        if (findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("E-mail ja cadastrado.");
        }
        user.setId(DataFiles.nextId(findAll().stream().map(User::getId).toList()));
        List<String> lines = Files.readAllLines(path);
        lines.add(toCsv(user));
        DataFiles.writeAtomically(path, lines);
        return user;
    }

    public synchronized List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                users.add(fromCsv(lines.get(i)));
            }
        }
        return users;
    }

    public synchronized User findByEmail(String email) throws IOException {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return findAll().stream().filter(user -> user.getEmail().equals(normalized)).findFirst().orElse(null);
    }

    public synchronized User findById(long id) throws IOException {
        return findAll().stream().filter(user -> user.getId() == id).findFirst().orElse(null);
    }

    public synchronized void update(User updated) throws IOException {
        List<User> users = findAll();
        User current = users.stream().filter(user -> user.getId().equals(updated.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
        boolean duplicatedEmail = users.stream().anyMatch(user ->
                !user.getId().equals(updated.getId()) && user.getEmail().equals(updated.getEmail()));
        if (duplicatedEmail) {
            throw new IllegalArgumentException("E-mail ja cadastrado.");
        }
        current.setName(updated.getName());
        current.setEmail(updated.getEmail());
        current.setPasswordHash(updated.getPasswordHash());
        current.setRole(updated.getRole());
        current.setActive(updated.isActive());
        saveAll(users);
    }

    public synchronized void delete(long id) throws IOException {
        List<User> users = findAll();
        if (!users.removeIf(user -> user.getId() == id)) {
            throw new IllegalArgumentException("Usuario nao encontrado.");
        }
        saveAll(users);
    }

    private void saveAll(List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        users.forEach(user -> lines.add(toCsv(user)));
        DataFiles.writeAtomically(path, lines);
    }

    private String toCsv(User user) {
        return CsvUtil.line(user.getId(), user.getName(), user.getEmail(), user.getPasswordHash(),
                user.getRole(), user.isActive());
    }

    private User fromCsv(String csv) {
        List<String> data = CsvUtil.parse(csv);
        return new User(Long.parseLong(data.get(0)), data.get(1), data.get(2), data.get(3),
                Role.valueOf(data.get(4)), Boolean.parseBoolean(data.get(5)));
    }
}
