package com.pi;

import com.pi.model.Client;
import com.pi.model.PaymentMethod;
import com.pi.model.Product;
import com.pi.model.Sale;
import com.pi.model.SaleStatus;
import com.pi.repository.AuditRepository;
import com.pi.repository.ClientRepository;
import com.pi.repository.ProductRepository;
import com.pi.repository.SaleRepository;
import com.pi.repository.UserRepository;
import com.pi.service.AuthService;
import com.pi.service.ReportService;
import com.pi.service.SaleService;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

public class IntegrationTest {
    public static void main(String[] args) throws Exception {
        UserRepository users = new UserRepository();
        ClientRepository clients = new ClientRepository();
        ProductRepository products = new ProductRepository();
        SaleRepository sales = new SaleRepository();
        AuditRepository audit = new AuditRepository();
        SaleService saleService = new SaleService(sales, products, clients, audit);
        ReportService reports = new ReportService(sales, clients);

        var session = new AuthService(users).login("seller@email.com", "111111");
        assertEquals("SELLER", session.getUser().getRole().name(), "perfil autenticado");

        Client client = clients.create(new Client(null, "Cliente Teste", "52998224725",
                "11999999999", "cliente@teste.com", "Instagram"));
        Product product = products.findById(1L);
        int initialStock = product.getStock();

        Sale sale = saleService.register(client.getId(), session.getUser().getEmail(),
                Map.of(product.getId(), 2));
        assertEquals(SaleStatus.PAYMENT_PENDING, sale.getStatus(), "status inicial");
        assertEquals(initialStock - 2, products.findById(product.getId()).getStock(), "baixa de estoque");

        sale = saleService.processPayment(sale.getId(), PaymentMethod.CASH,
                sale.getTotal() + 10, session.getUser().getEmail());
        assertEquals(SaleStatus.COMPLETED, sale.getStatus(), "pagamento aprovado");
        assertTrue(Files.exists(saleService.issueReceipt(sale.getId())), "comprovante");
        assertTrue(reports.revenueBetween(LocalDate.now(), LocalDate.now()) > 0, "faturamento");

        saleService.cancel(sale.getId(), "manager@email.com");
        assertEquals(SaleStatus.CANCELLED, sales.findById(sale.getId()).getStatus(), "cancelamento");
        assertEquals(initialStock, products.findById(product.getId()).getStock(), "reposicao de estoque");

        System.out.println("Teste de integracao concluido com sucesso.");
    }

    private static void assertEquals(Object expected, Object actual, String field) {
        if (!expected.equals(actual)) {
            throw new AssertionError(field + ": esperado " + expected + ", obtido " + actual);
        }
    }

    private static void assertTrue(boolean condition, String field) {
        if (!condition) {
            throw new AssertionError(field + ": condicao nao atendida");
        }
    }
}
