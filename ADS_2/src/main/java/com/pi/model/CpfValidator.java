package com.pi.model;

public class CpfValidator {

    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        String digits = cpf.replaceAll("\\D", "");

        if (digits.length() != 11 || digits.matches("(\\d)\\1{10}")) {
            return false;
        }

        int d1 = calculateVerifierDigit(digits, 9, 10);
        int d2 = calculateVerifierDigit(digits, 10, 11);

        return d1 == Character.getNumericValue(digits.charAt(9))
                && d2 == Character.getNumericValue(digits.charAt(10));
    }

    private static int calculateVerifierDigit(String digits, int length, int weightStart) {
        int sum = 0;
        int weight = weightStart;

        for (int i = 0; i < length; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * weight--;
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
