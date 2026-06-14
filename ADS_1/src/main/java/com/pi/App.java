package com.pi;

import com.pi.model.Client;
import com.pi.model.PaymentMethod;
import com.pi.model.Product;
import com.pi.model.Role;
import com.pi.model.Sale;
import com.pi.model.SaleItem;
import com.pi.model.Supplier;
import com.pi.model.User;
import com.pi.repository.AuditRepository;
import com.pi.repository.ClientRepository;
import com.pi.repository.ProductRepository;
import com.pi.repository.SaleRepository;
import com.pi.repository.SupplierRepository;
import com.pi.repository.UserRepository;
import com.pi.service.AuthService;
import com.pi.service.BackupService;
import com.pi.service.PasswordUtil;
import com.pi.service.ReportService;
import com.pi.service.SaleService;
import com.pi.service.Session;
import com.pi.ui.ConsoleInput;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ConsoleInput input;
    private final UserRepository users;
    private final ClientRepository clients;
    private final ProductRepository products;
    private final SupplierRepository suppliers;
    private final SaleRepository sales;
    private final AuthService auth;
    private final SaleService saleService;
    private final ReportService reports;

    public App(Scanner scanner) throws IOException {
        input = new ConsoleInput(scanner);
        users = new UserRepository();
        clients = new ClientRepository();
        products = new ProductRepository();
        suppliers = new SupplierRepository();
        sales = new SaleRepository();
        AuditRepository audit = new AuditRepository();
        auth = new AuthService(users);
        saleService = new SaleService(sales, products, clients, audit);
        reports = new ReportService(sales, clients);
        new BackupService().createDailyBackup();
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new App(scanner).run();
        } catch (Exception exception) {
            System.err.println("Falha ao iniciar: " + exception.getMessage());
        }
    }

    public void run() {
        System.out.println("=== SISTEMA DE CONTROLE DE VENDAS ===");
        while (true) {
            try {
                System.out.println("\n1. Fazer login\n0. Encerrar");
                int option = input.integer("Opcao: ");
                if (option == 0) {
                    System.out.println("Sistema encerrado.");
                    return;
                }
                if (option != 1) {
                    System.out.println("Opcao invalida.");
                    continue;
                }
                Session session = auth.login(input.required("E-mail: "), input.required("Senha: "));
                authenticatedMenu(session);
            } catch (Exception exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private void authenticatedMenu(Session session) {
        while (!session.isExpired()) {
            try {
                User user = session.getUser();
                System.out.printf("%nUsuario: %s | Perfil: %s%n", user.getName(), user.getRole());
                boolean logout = switch (user.getRole()) {
                    case ADMIN -> adminMenu(user);
                    case MANAGER -> managerMenu(user);
                    case SELLER -> sellerMenu(user);
                };
                if (logout) {
                    session.close();
                    System.out.println("Logout realizado.");
                    return;
                }
            } catch (Exception exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
        System.out.println("Sessao expirada. Faca login novamente.");
    }

    private boolean adminMenu(User user) throws IOException {
        System.out.println("""
                1. Gerenciar produtos
                2. Gerenciar fornecedores
                3. Gerenciar usuarios
                4. Consultar estoque
                0. Logout""");
        return switch (input.integer("Opcao: ")) {
            case 1 -> {
                productMenu();
                yield false;
            }
            case 2 -> {
                supplierMenu();
                yield false;
            }
            case 3 -> {
                userMenu(user);
                yield false;
            }
            case 4 -> {
                listStock();
                yield false;
            }
            case 0 -> true;
            default -> {
                System.out.println("Opcao invalida.");
                yield false;
            }
        };
    }

    private boolean managerMenu(User user) throws IOException {
        System.out.println("""
                1. Relatorio de vendas
                2. Consultar faturamento
                3. Historico de vendas
                4. Cancelar venda
                5. Consultar estoque
                6. Relatorio de origem dos clientes
                0. Logout""");
        return switch (input.integer("Opcao: ")) {
            case 1 -> {
                showSalesReport();
                yield false;
            }
            case 2 -> {
                showRevenue();
                yield false;
            }
            case 3 -> {
                showSalesReport();
                yield false;
            }
            case 4 -> {
                saleService.cancel(input.longNumber("ID da venda: "), user.getEmail());
                System.out.println("Venda cancelada e estoque devolvido.");
                yield false;
            }
            case 5 -> {
                listStock();
                yield false;
            }
            case 6 -> {
                showOrigins();
                yield false;
            }
            case 0 -> true;
            default -> {
                System.out.println("Opcao invalida.");
                yield false;
            }
        };
    }

    private boolean sellerMenu(User user) throws IOException {
        System.out.println("""
                1. Gerenciar clientes
                2. Consultar estoque
                3. Registrar venda
                4. Processar pagamento pendente
                5. Emitir comprovante
                6. Consultar historico de vendas
                0. Logout""");
        return switch (input.integer("Opcao: ")) {
            case 1 -> {
                clientMenu();
                yield false;
            }
            case 2 -> {
                listStock();
                yield false;
            }
            case 3 -> {
                registerSale(user);
                yield false;
            }
            case 4 -> {
                processPayment(input.longNumber("ID da venda: "), user);
                yield false;
            }
            case 5 -> {
                Path receipt = saleService.issueReceipt(input.longNumber("ID da venda: "));
                System.out.println("Comprovante emitido em " + receipt);
                yield false;
            }
            case 6 -> {
                showSalesReport();
                yield false;
            }
            case 0 -> true;
            default -> {
                System.out.println("Opcao invalida.");
                yield false;
            }
        };
    }

    private void productMenu() throws IOException {
        System.out.println("1. Cadastrar  2. Listar/consultar  3. Editar  4. Excluir  5. Repor estoque");
        switch (input.integer("Opcao: ")) {
            case 1 -> {
                Product product = products.create(readProduct(null));
                System.out.println("Produto cadastrado com ID " + product.getId() + ".");
            }
            case 2 -> listProducts(products.search(input.text("Nome ou ID (vazio para todos): ")));
            case 3 -> {
                long id = input.longNumber("ID: ");
                products.update(id, readProduct(id));
                System.out.println("Produto atualizado.");
            }
            case 4 -> {
                products.delete(input.longNumber("ID: "));
                System.out.println("Produto excluido.");
            }
            case 5 -> {
                products.changeStock(input.longNumber("ID: "), input.integer("Quantidade recebida: "));
                System.out.println("Estoque atualizado.");
            }
            default -> System.out.println("Opcao invalida.");
        }
    }

    private Product readProduct(Long id) {
        return new Product(id, input.required("Nome: "), input.decimal("Preco: "),
                input.integer("Estoque: "));
    }

    private void listProducts(List<Product> found) {
        if (found.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }
        found.forEach(product -> System.out.printf(
                "#%d | %s | R$ %.2f | estoque: %d%n",
                product.getId(), product.getName(), product.getPrice(), product.getStock()));
    }

    private void listStock() throws IOException {
        listProducts(products.search(input.text("Nome ou ID (vazio para todos): ")));
    }

    private void clientMenu() throws IOException {
        System.out.println("1. Cadastrar  2. Consultar  3. Editar  4. Excluir");
        switch (input.integer("Opcao: ")) {
            case 1 -> {
                Client client = clients.create(readClient(null));
                System.out.println("Cliente cadastrado com ID " + client.getId() + ".");
            }
            case 2 -> listClients(clients.search(input.text("Nome, CPF ou ID: ")));
            case 3 -> {
                long id = input.longNumber("ID: ");
                if (clients.findById(id) == null) {
                    throw new IllegalArgumentException("Cliente nao encontrado.");
                }
                clients.update(readClient(id));
                System.out.println("Cliente atualizado.");
            }
            case 4 -> {
                clients.delete(input.longNumber("ID: "));
                System.out.println("Cliente excluido.");
            }
            default -> System.out.println("Opcao invalida.");
        }
    }

    private Client readClient(Long id) {
        String origin = input.required("Origem (Instagram, indicacao, Google, outro...): ");
        if (origin.equalsIgnoreCase("outro")) {
            origin = "Outro: " + input.required("Descreva a origem: ");
        }
        return new Client(id, input.required("Nome: "), input.required("CPF: "),
                input.text("Telefone: "), input.required("E-mail: "), origin);
    }

    private void listClients(List<Client> found) {
        if (found.isEmpty()) {
            System.out.println("Nenhum cliente encontrado.");
            return;
        }
        found.forEach(client -> System.out.printf(
                "#%d | %s | CPF %s | %s | origem: %s%n",
                client.getId(), client.getName(), client.getCpf(), client.getEmail(), client.getOrigin()));
    }

    private void supplierMenu() throws IOException {
        System.out.println("1. Cadastrar  2. Listar  3. Editar  4. Excluir");
        switch (input.integer("Opcao: ")) {
            case 1 -> {
                Supplier supplier = suppliers.create(readSupplier(null));
                System.out.println("Fornecedor cadastrado com ID " + supplier.getId() + ".");
            }
            case 2 -> suppliers.findAll().forEach(this::printSupplier);
            case 3 -> {
                long id = input.longNumber("ID: ");
                if (suppliers.findById(id) == null) {
                    throw new IllegalArgumentException("Fornecedor nao encontrado.");
                }
                suppliers.update(readSupplier(id));
                System.out.println("Fornecedor atualizado.");
            }
            case 4 -> {
                suppliers.delete(input.longNumber("ID: "));
                System.out.println("Fornecedor excluido.");
            }
            default -> System.out.println("Opcao invalida.");
        }
    }

    private Supplier readSupplier(Long id) {
        return new Supplier(id, input.required("Nome: "), input.required("CNPJ: "),
                input.text("Telefone: "), input.required("E-mail: "));
    }

    private void printSupplier(Supplier supplier) {
        System.out.printf("#%d | %s | CNPJ %s | %s%n",
                supplier.getId(), supplier.getName(), supplier.getCnpj(), supplier.getEmail());
    }

    private void userMenu(User loggedUser) throws IOException {
        System.out.println("1. Cadastrar  2. Listar  3. Editar  4. Remover");
        switch (input.integer("Opcao: ")) {
            case 1 -> {
                String password = input.required("Senha (minimo 6 caracteres): ");
                User user = users.create(new User(null, input.required("Nome: "),
                        input.required("E-mail: "), PasswordUtil.hash(password),
                        readRole(), input.yesNo("Usuario ativo")));
                System.out.println("Usuario cadastrado com ID " + user.getId() + ".");
            }
            case 2 -> users.findAll().forEach(this::printUser);
            case 3 -> updateUser();
            case 4 -> {
                long id = input.longNumber("ID: ");
                if (loggedUser.getId() == id) {
                    throw new IllegalArgumentException("Nao e permitido remover o proprio usuario.");
                }
                users.delete(id);
                System.out.println("Usuario removido.");
            }
            default -> System.out.println("Opcao invalida.");
        }
    }

    private void updateUser() throws IOException {
        long id = input.longNumber("ID: ");
        User current = users.findById(id);
        if (current == null) {
            throw new IllegalArgumentException("Usuario nao encontrado.");
        }
        String password = input.text("Nova senha (vazio para manter): ");
        current.setName(input.required("Nome: "));
        current.setEmail(input.required("E-mail: "));
        if (!password.isBlank()) {
            current.setPasswordHash(PasswordUtil.hash(password));
        }
        current.setRole(readRole());
        current.setActive(input.yesNo("Usuario ativo"));
        users.update(current);
        System.out.println("Usuario atualizado.");
    }

    private Role readRole() {
        System.out.println("1. Administrador  2. Gerente  3. Vendedor");
        return switch (input.integer("Perfil: ")) {
            case 1 -> Role.ADMIN;
            case 2 -> Role.MANAGER;
            case 3 -> Role.SELLER;
            default -> throw new IllegalArgumentException("Perfil invalido.");
        };
    }

    private void printUser(User user) {
        System.out.printf("#%d | %s | %s | %s | %s%n",
                user.getId(), user.getName(), user.getEmail(), user.getRole(),
                user.isActive() ? "ativo" : "inativo");
    }

    private void registerSale(User seller) throws IOException {
        listClients(clients.findAll());
        long clientId = input.longNumber("ID do cliente: ");
        Map<Long, Integer> cart = new LinkedHashMap<>();
        while (true) {
            listProducts(products.findAll());
            long productId = input.longNumber("ID do produto: ");
            int quantity = input.integer("Quantidade: ");
            cart.merge(productId, quantity, Integer::sum);
            if (!input.yesNo("Adicionar outro produto")) {
                break;
            }
        }
        Sale sale = saleService.register(clientId, seller.getEmail(), cart);
        System.out.printf("Venda #%d registrada. Total: R$ %.2f%n", sale.getId(), sale.getTotal());
        if (input.yesNo("Processar pagamento agora")) {
            processPayment(sale.getId(), seller);
        }
    }

    private void processPayment(long saleId, User operator) throws IOException {
        Sale sale = sales.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("Venda nao encontrada.");
        }
        System.out.printf("Total: R$ %.2f%n", sale.getTotal());
        PaymentMethod method = readPaymentMethod();
        double received = method == PaymentMethod.CASH
                ? input.decimal("Valor recebido: ") : sale.getTotal();
        sale = saleService.processPayment(saleId, method, received, operator.getEmail());
        System.out.println("Pagamento aprovado.");
        if (input.yesNo("Emitir comprovante")) {
            System.out.println("Comprovante emitido em " + saleService.issueReceipt(sale.getId()));
        }
    }

    private PaymentMethod readPaymentMethod() {
        System.out.println("1. Dinheiro  2. Pix  3. Credito  4. Debito");
        return switch (input.integer("Forma de pagamento: ")) {
            case 1 -> PaymentMethod.CASH;
            case 2 -> PaymentMethod.PIX;
            case 3 -> PaymentMethod.CREDIT_CARD;
            case 4 -> PaymentMethod.DEBIT_CARD;
            default -> throw new IllegalArgumentException("Forma de pagamento invalida.");
        };
    }

    private void showSalesReport() throws IOException {
        LocalDate start = input.date("Data inicial");
        LocalDate end = input.date("Data final");
        List<Sale> found = reports.salesBetween(start, end);
        if (found.isEmpty()) {
            System.out.println("Nenhuma venda encontrada no periodo.");
            return;
        }
        found.forEach(this::printSale);
    }

    private void showRevenue() throws IOException {
        LocalDate start = input.date("Data inicial");
        LocalDate end = input.date("Data final");
        System.out.printf("Faturamento no periodo: R$ %.2f%n", reports.revenueBetween(start, end));
    }

    private void showOrigins() throws IOException {
        Map<String, Long> origins = reports.clientOrigins();
        if (origins.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        origins.forEach((origin, quantity) -> System.out.printf("%s: %d cliente(s)%n", origin, quantity));
    }

    private void printSale(Sale sale) {
        System.out.printf("%nVenda #%d | %s | cliente #%d | %s | R$ %.2f%n",
                sale.getId(), sale.getCreatedAt().format(DATE_TIME), sale.getClientId(),
                sale.getStatus(), sale.getTotal());
        for (SaleItem item : sale.getItems()) {
            System.out.printf("  %s: %d x R$ %.2f%n",
                    item.productName(), item.quantity(), item.unitPrice());
        }
    }
}
