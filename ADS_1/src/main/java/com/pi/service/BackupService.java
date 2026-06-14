package com.pi.service;

import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class BackupService {
    public void createDailyBackup() throws IOException {
        Path dataDirectory = DataFiles.data("Accounts.csv").getParent();
        Path backupDirectory = dataDirectory.resolve("backups").resolve(LocalDate.now().toString());
        if (Files.exists(backupDirectory)) {
            return;
        }
        Files.createDirectories(backupDirectory);
        try (var files = Files.list(dataDirectory)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".csv")).toList()) {
                Files.copy(file, backupDirectory.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
