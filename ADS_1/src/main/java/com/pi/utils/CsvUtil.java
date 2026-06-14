package com.pi.utils;

import java.util.ArrayList;
import java.util.List;

public final class CsvUtil {
    private CsvUtil() {
    }

    public static String line(Object... values) {
        List<String> fields = new ArrayList<>();
        for (Object value : values) {
            fields.add(escape(value == null ? "" : value.toString()));
        }
        return String.join(",", fields);
    }

    public static List<String> parse(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
