package com.pi.model;

public class Cpf {
    private static final int[] weightCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private final String cpf;

    public Cpf(String cpf) {
        if (!validateCpf(cpf))
            throw new IllegalArgumentException("Invalid CPF");
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    private static int calculateDigit(String str, int[] weight) {
        int sum = 0;
        for (int indice=str.length()-1, digit; indice >= 0; indice-- ) {
            digit = Integer.parseInt(str.substring(indice,indice+1));
            sum += digit*weight[weight.length-str.length()+indice];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }

    private static String padLeft(String text, char character) {
        return String.format("%11s", text).replace(' ', character);
    }

    public boolean validateCpf(String cpf) {
        if ((cpf==null) || (cpf.length()!=11)) return false;

        for (int j = 0; j < 10; j++)
            if (padLeft(Integer.toString(j), Character.forDigit(j, 10)).equals(cpf))
                return false;

        Integer digit1 = calculateDigit(cpf.substring(0,9), weightCPF);
        Integer digit2 = calculateDigit(cpf.substring(0,9) + digit1, weightCPF);
        return cpf.equals(cpf.substring(0,9) + digit1.toString() + digit2.toString());
    }
}