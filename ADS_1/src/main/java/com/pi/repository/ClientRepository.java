package com.pi.repository;

import com.pi.model.Client;
import com.pi.utils.CsvUtil;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    private static final String HEADER = "id,name,cpf,phone,email,origin";
    private final Path path;

    public ClientRepository() throws IOException {
        path = DataFiles.data("Clients.csv");
        DataFiles.ensure(path, HEADER);
    }

    public synchronized Client create(Client client) throws IOException {
        if (findByCpf(client.getCpf()) != null) {
            throw new IllegalArgumentException("CPF ja cadastrado.");
        }
        client.setId(DataFiles.nextId(findAll().stream().map(Client::getId).toList()));
        List<String> lines = Files.readAllLines(path);
        lines.add(toCsv(client));
        DataFiles.writeAtomically(path, lines);
        return client;
    }

    public synchronized List<Client> findAll() throws IOException {
        List<Client> clients = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                clients.add(fromCsv(lines.get(i)));
            }
        }
        return clients;
    }

    public synchronized Client findById(long id) throws IOException {
        return findAll().stream().filter(client -> client.getId() == id).findFirst().orElse(null);
    }

    public synchronized Client findByCpf(String cpf) throws IOException {
        String normalized = cpf == null ? "" : cpf.replaceAll("\\D", "");
        return findAll().stream().filter(client -> client.getCpf().equals(normalized)).findFirst().orElse(null);
    }

    public synchronized List<Client> search(String term) throws IOException {
        String normalized = term == null ? "" : term.toLowerCase();
        String digits = term == null ? "" : term.replaceAll("\\D", "");
        return findAll().stream()
                .filter(client -> client.getName().toLowerCase().contains(normalized)
                        || client.getCpf().contains(digits)
                        || client.getId().toString().equals(normalized))
                .toList();
    }

    public synchronized void update(Client updated) throws IOException {
        List<Client> clients = findAll();
        Client current = clients.stream().filter(client -> client.getId().equals(updated.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
        boolean duplicatedCpf = clients.stream().anyMatch(client ->
                !client.getId().equals(updated.getId()) && client.getCpf().equals(updated.getCpf()));
        if (duplicatedCpf) {
            throw new IllegalArgumentException("CPF ja cadastrado.");
        }
        current.setName(updated.getName());
        current.setCpf(updated.getCpf());
        current.setPhone(updated.getPhone());
        current.setEmail(updated.getEmail());
        current.setOrigin(updated.getOrigin());
        saveAll(clients);
    }

    public synchronized void delete(Long id) throws IOException {
        List<Client> clients = findAll();
        if (!clients.removeIf(client -> client.getId().equals(id))) {
            throw new IllegalArgumentException("Cliente nao encontrado.");
        }
        saveAll(clients);
    }

    private void saveAll(List<Client> clients) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        clients.forEach(client -> lines.add(toCsv(client)));
        DataFiles.writeAtomically(path, lines);
    }

    private String toCsv(Client client) {
        return CsvUtil.line(client.getId(), client.getName(), client.getCpf(),
                client.getPhone(), client.getEmail(), client.getOrigin());
    }

    private Client fromCsv(String csv) {
        List<String> data = CsvUtil.parse(csv);
        return new Client(Long.parseLong(data.get(0)), data.get(1), data.get(2),
                data.get(3), data.get(4), data.get(5));
    }
}
