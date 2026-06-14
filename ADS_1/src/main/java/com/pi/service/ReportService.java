package com.pi.service;

import com.pi.model.Client;
import com.pi.model.Sale;
import com.pi.model.SaleStatus;
import com.pi.repository.ClientRepository;
import com.pi.repository.SaleRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;

    public ReportService(SaleRepository saleRepository, ClientRepository clientRepository) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
    }

    public List<Sale> salesBetween(LocalDate start, LocalDate end) throws IOException {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("O fim do periodo deve ser posterior ao inicio.");
        }
        return saleRepository.findAll().stream()
                .filter(sale -> !sale.getCreatedAt().toLocalDate().isBefore(start)
                        && !sale.getCreatedAt().toLocalDate().isAfter(end))
                .sorted(Comparator.comparing(Sale::getCreatedAt).reversed())
                .toList();
    }

    public double revenueBetween(LocalDate start, LocalDate end) throws IOException {
        return salesBetween(start, end).stream()
                .filter(sale -> sale.getStatus() == SaleStatus.COMPLETED)
                .mapToDouble(Sale::getTotal)
                .sum();
    }

    public Map<String, Long> clientOrigins() throws IOException {
        return clientRepository.findAll().stream()
                .collect(Collectors.groupingBy(Client::getOrigin, LinkedHashMap::new, Collectors.counting()));
    }
}
