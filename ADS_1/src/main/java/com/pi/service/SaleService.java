package com.pi.service;

import com.pi.model.Client;
import com.pi.model.PaymentMethod;
import com.pi.model.Product;
import com.pi.model.Sale;
import com.pi.model.SaleItem;
import com.pi.model.SaleStatus;
import com.pi.repository.AuditRepository;
import com.pi.repository.ClientRepository;
import com.pi.repository.ProductRepository;
import com.pi.repository.SaleRepository;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaleService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final AuditRepository auditRepository;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository,
                       ClientRepository clientRepository, AuditRepository auditRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.auditRepository = auditRepository;
    }

    public synchronized Sale register(long clientId, String sellerEmail,
                                      Map<Long, Integer> requestedItems) throws IOException {
        Client client = clientRepository.findById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Cliente nao encontrado.");
        }
        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um produto.");
        }

        List<SaleItem> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> requested : requestedItems.entrySet()) {
            Product product = productRepository.findById(requested.getKey());
            int quantity = requested.getValue();
            if (product == null) {
                throw new IllegalArgumentException("Produto " + requested.getKey() + " nao encontrado.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser positiva.");
            }
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("Estoque insuficiente para " + product.getName() + ".");
            }
            items.add(new SaleItem(product.getId(), product.getName(), quantity, product.getPrice()));
        }

        for (SaleItem item : items) {
            productRepository.changeStock(item.productId(), -item.quantity());
        }
        Sale sale = new Sale(null, clientId, sellerEmail, LocalDateTime.now(),
                SaleStatus.PAYMENT_PENDING, null, 0, items);
        try {
            return saleRepository.create(sale);
        } catch (IOException | RuntimeException exception) {
            for (SaleItem item : items) {
                productRepository.changeStock(item.productId(), item.quantity());
            }
            throw exception;
        }
    }

    public synchronized Sale processPayment(long saleId, PaymentMethod method,
                                            double amountReceived, String operator) throws IOException {
        Sale sale = requireSale(saleId);
        if (sale.getStatus() != SaleStatus.PAYMENT_PENDING) {
            throw new IllegalArgumentException("A venda nao esta aguardando pagamento.");
        }
        if (method == PaymentMethod.CASH && amountReceived < sale.getTotal()) {
            auditRepository.log(operator, "PAYMENT_REJECTED",
                    "Venda " + saleId + ": valor insuficiente");
            throw new IllegalArgumentException("Valor recebido e insuficiente.");
        }
        if (amountReceived < 0) {
            throw new IllegalArgumentException("Valor recebido invalido.");
        }

        sale.setPaymentMethod(method);
        sale.setAmountReceived(method == PaymentMethod.CASH ? amountReceived : sale.getTotal());
        sale.setStatus(SaleStatus.COMPLETED);
        saleRepository.update(sale);
        auditRepository.log(operator, "PAYMENT_APPROVED",
                "Venda " + saleId + ", total " + String.format("%.2f", sale.getTotal())
                        + ", forma " + method);
        return sale;
    }

    public synchronized void cancel(long saleId, String managerEmail) throws IOException {
        Sale sale = requireSale(saleId);
        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new IllegalArgumentException("A venda ja esta cancelada.");
        }
        for (SaleItem item : sale.getItems()) {
            productRepository.changeStock(item.productId(), item.quantity());
        }
        sale.setStatus(SaleStatus.CANCELLED);
        saleRepository.update(sale);
        auditRepository.log(managerEmail, "SALE_CANCELLED", "Venda " + saleId);
    }

    public Path issueReceipt(long saleId) throws IOException {
        Sale sale = requireSale(saleId);
        if (sale.getStatus() != SaleStatus.COMPLETED) {
            throw new IllegalArgumentException("O comprovante exige pagamento aprovado.");
        }
        Client client = clientRepository.findById(sale.getClientId());
        Path directory = DataFiles.data("receipts").getParent().resolve("receipts");
        Files.createDirectories(directory);
        Path receipt = directory.resolve("sale-" + sale.getId() + ".txt");
        StringBuilder body = new StringBuilder();
        body.append("COMPROVANTE DE VENDA #").append(sale.getId()).append("\n")
                .append("Data: ").append(sale.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n")
                .append("Cliente: ").append(client == null ? sale.getClientId() : client.getName()).append("\n")
                .append("Vendedor: ").append(sale.getSellerEmail()).append("\n\n");
        for (SaleItem item : sale.getItems()) {
            body.append(String.format("%s - %d x R$ %.2f = R$ %.2f%n",
                    item.productName(), item.quantity(), item.unitPrice(), item.subtotal()));
        }
        body.append(String.format("%nTOTAL: R$ %.2f%n", sale.getTotal()))
                .append("Pagamento: ").append(sale.getPaymentMethod().getDescription()).append("\n");
        if (sale.getPaymentMethod() == PaymentMethod.CASH) {
            body.append(String.format("Recebido: R$ %.2f%nTroco: R$ %.2f%n",
                    sale.getAmountReceived(), sale.getAmountReceived() - sale.getTotal()));
        }
        Files.writeString(receipt, body.toString());
        return receipt;
    }

    private Sale requireSale(long id) throws IOException {
        Sale sale = saleRepository.findById(id);
        if (sale == null) {
            throw new IllegalArgumentException("Venda nao encontrada.");
        }
        return sale;
    }
}
