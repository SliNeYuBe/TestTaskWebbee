package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.CreateItems.*;
import static core.GetItems.*;

public class CreateLogs {
    static Map<String, List<String>> createLogs(List<Path> paths) throws IOException {
        Map<String, List<String>> userLogs = new HashMap<>();
        for (Path path : paths) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String logUser1;
                while ((logUser1 = reader.readLine()) != null) {
                    String user1 = getUser(logUser1, 1);
                    if (logUser1.contains(OperationType.TRANSFERRED.getOperation())) {
                        String user2 = getUser(logUser1, 2);
                        double money = getMoney(logUser1, OperationType.TRANSFERRED);
                        String time = getTime(logUser1);
                        String logUser2 = createLog(time, user1, user2, money);
                        userLogs.computeIfAbsent(user2, _ -> new ArrayList<>()).add(logUser2);
                    }
                    userLogs.computeIfAbsent(user1, _ -> new ArrayList<>()).add(logUser1);
                }
            }
        }
        return userLogs;
    }
}
