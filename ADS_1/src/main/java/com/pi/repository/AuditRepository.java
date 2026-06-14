package com.pi.repository;

import com.pi.utils.CsvUtil;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class AuditRepository {
    private final Path path;

    public AuditRepository() throws IOException {
        path = DataFiles.data("Audit.csv");
        DataFiles.ensure(path, "timestamp,user,operation,details");
    }

    public synchronized void log(String user, String operation, String details) throws IOException {
        Files.writeString(path, CsvUtil.line(LocalDateTime.now(), user, operation, details)
                        + System.lineSeparator(),
                StandardOpenOption.APPEND);
    }
}
