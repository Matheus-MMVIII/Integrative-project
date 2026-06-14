package com.pi.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class DataFiles {
    private DataFiles() {
    }

    public static Path data(String fileName) {
        Path fromProjectRoot = Path.of("ADS_1", "data", fileName);
        if (Files.exists(fromProjectRoot.getParent())) {
            return fromProjectRoot;
        }
        return Path.of("data", fileName);
    }

    public static void ensure(Path path, String header) throws IOException {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path) || Files.size(path) == 0) {
            Files.writeString(path, header + System.lineSeparator());
        }
    }

    public static void writeAtomically(Path path, List<String> lines) throws IOException {
        Path temporary = Files.createTempFile(path.getParent(), path.getFileName().toString(), ".tmp");
        Files.write(temporary, lines);
        try {
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException unsupportedAtomicMove) {
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static long nextId(List<Long> ids) {
        return ids.stream().mapToLong(Long::longValue).max().orElse(0L) + 1L;
    }
}
