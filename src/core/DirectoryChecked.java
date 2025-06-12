package core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryChecked {
    static boolean checkedDirectory(Path transactionPath) throws IOException { //Проверяет, есть ли в папке логи
        if (Files.exists(transactionPath) && Files.isDirectory(transactionPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(transactionPath)) {
                for (Path file : stream) {
                    try {
                        if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".log")) {
                            return true;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return false;
    }
}
