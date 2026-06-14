package com.pi;

import com.pi.model.Administrator;
import com.pi.model.Login;
import com.pi.model.Product;
import com.pi.repository.ProductRepository;

import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException {
        Administrator administrator = new Administrator(new ProductRepository());
        administrator.createProduct(new Product("Test", 20.0d, 10));
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                switch (login(sc)) {
                    case "ADMIN" ->
                    case "MANAGER" ->
                    case "SELLER" ->
                    defalt -> System.out.println("Login invalido!");
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String login(Scanner sc) throws IOException {
        System.out.println("Email:");
        String email = sc.nextLine();

        System.out.println("Senha:");
        String password = sc.nextLine();

        Login login = new Login(email, password);
        return login.getType();

    }
}