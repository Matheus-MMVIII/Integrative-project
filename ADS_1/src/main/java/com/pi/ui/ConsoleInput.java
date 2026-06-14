package com.pi.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleInput {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String text(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    public String required(String label) {
        while (true) {
            String value = text(label);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Este campo e obrigatorio.");
        }
    }

    public long longNumber(String label) {
        while (true) {
            try {
                return Long.parseLong(required(label));
            } catch (NumberFormatException exception) {
                System.out.println("Informe um numero inteiro valido.");
            }
        }
    }

    public int integer(String label) {
        while (true) {
            try {
                return Integer.parseInt(required(label));
            } catch (NumberFormatException exception) {
                System.out.println("Informe um numero inteiro valido.");
            }
        }
    }

    public double decimal(String label) {
        while (true) {
            try {
                return Double.parseDouble(required(label).replace(",", "."));
            } catch (NumberFormatException exception) {
                System.out.println("Informe um valor numerico valido.");
            }
        }
    }

    public boolean yesNo(String label) {
        while (true) {
            String value = required(label + " (S/N): ").toUpperCase();
            if (value.equals("S")) {
                return true;
            }
            if (value.equals("N")) {
                return false;
            }
            System.out.println("Responda com S ou N.");
        }
    }

    public LocalDate date(String label) {
        while (true) {
            try {
                return LocalDate.parse(required(label + " (dd/MM/aaaa): "), DATE_FORMAT);
            } catch (DateTimeParseException exception) {
                System.out.println("Data invalida.");
            }
        }
    }
}
