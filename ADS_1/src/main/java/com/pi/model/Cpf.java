package com.pi.model;

public class Cpf {
    private static final int[] WEIGHTS = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private final String cpf;

    public Cpf(String cpf) {
        String normalized = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (!validateCpf(normalized)) {
            throw new IllegalArgumentException("CPF invalido.");
        }
        this.cpf = normalized;
    }

    public String getCpf() {
        return cpf;
    }

    private static int calculateDigit(String value) {
        int sum = 0;
        int offset = WEIGHTS.length - value.length();
        for (int i = 0; i < value.length(); i++) {
            sum += Character.digit(value.charAt(i), 10) * WEIGHTS[offset + i];
        }
        int digit = 11 - sum % 11;
        return digit > 9 ? 0 : digit;
    }

    public static boolean validateCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int first = calculateDigit(cpf.substring(0, 9));
        int second = calculateDigit(cpf.substring(0, 9) + first);
        return cpf.equals(cpf.substring(0, 9) + first + second);
    }
}
