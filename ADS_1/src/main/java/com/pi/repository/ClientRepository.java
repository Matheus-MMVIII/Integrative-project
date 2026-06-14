package com.pi.repository;

import com.pi.model.Client;
import com.pi.utils.CsvUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private final Path path = Path.of("ADS_1/data/Clients.csv");

    public void create(Client client) throws IOException {

        client.setId(nextId());

        Files.write(
                path,
                ("\n" + CsvUtil.client(client)).getBytes(),
                StandardOpenOption.APPEND
        );
    }

    public List<Client> findAll() throws IOException {

        List<Client> clients = new ArrayList<>();

        List<String> lines = Files.readAllLines(path);

        for (int i = 1; i < lines.size(); i++) {
            clients.add(CsvUtil.toClient(lines.get(i)));
        }

        return clients;
    }

    public Client findByCpf(String cpf) throws IOException {

        return findAll()
                .stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
    }

    public void update(Client client) throws IOException {
        // mesmo padrão do ProductRepository
    }

    public void delete(Long id) throws IOException {
        // mesmo padrão do ProductRepository
    }

    private Long nextId() throws IOException {
        return (long) Files.readAllLines(path).size();
    }
}
