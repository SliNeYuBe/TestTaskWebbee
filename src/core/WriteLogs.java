package core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static core.CreateItems.createFinalBalance;
import static core.TransactionCleaner.clearTransactionDirectory;
import static core.CreateLogs.createLogs;

public class WriteLogs {
    static void writeLogs(Path directoryPath, Path transactionPath) throws IOException {

        clearTransactionDirectory(transactionPath); //Пересоздает логи пользователей
        Files.createDirectories(transactionPath);

        List<Path> paths = new ArrayList<>(); //Создает лист с путями к заданным логам
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directoryPath)) {
            for (Path path : files) {
                if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".log")) paths.add(path);
            }
        }

        Map<String, List<String>> userLogs = createLogs(paths);

        for (Map.Entry<String, List<String>> entry : userLogs.entrySet()) { //Создает лог файл с именем юзера и заполняет все операции, которые он сделал
            String key = entry.getKey();
            List<String> value = entry.getValue().stream().sorted().collect(Collectors.toList());
            value.add(createFinalBalance(key, value));
            Path filePath = transactionPath.resolve(key + ".log");

            Files.write(filePath, value, StandardOpenOption.CREATE);
        }
    }
}