package core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TransactionCleaner {
    static void clearTransactionDirectory(Path transactionPath) throws IOException { //Пересоздает логи пользователей и заполняет их заново
        if (Files.exists(transactionPath) && Files.isDirectory(transactionPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(transactionPath)) {
                for (Path file : stream) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + file + " - " + e.getMessage());
                    }
                }
            }
        }
    }
}
