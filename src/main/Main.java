package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите абсолютный путь директории или exit, если хотите выйти: ");
        Path directoryPath = Path.of(sc.nextLine());

        while (!(Files.isDirectory(directoryPath) && directoryPath.isAbsolute())) {
            if (directoryPath.toString().equals("exit")) {
                return;
            }
            System.out.println("Вы ввели неверный путь. Введите его заново или exit, если хотите выйти: ");
            directoryPath = Path.of(sc.nextLine());
        }

        Path transactionPath = directoryPath.resolve("transaction_by_users");
        Files.createDirectories(transactionPath);

        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directoryPath)) {
            for (Path path : files) {
                if (Files.isRegularFile(path)) paths.add(path);
            }
        }

        Map<String, List<String>> userLogs = createLogs(paths);

        for (Map.Entry<String, List<String>> entry : userLogs.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue().stream().sorted().collect(Collectors.toList());
            value.add(createFinalBalance(key, value));
            Path filePath = transactionPath.resolve(key + ".log");

            Files.write(filePath, value, StandardOpenOption.CREATE);
        }
    }

    private static Map<String, List<String>> createLogs(List<Path> paths) throws IOException {
        Map<String, List<String>> userLogs = new HashMap<>();
        for (Path path : paths) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String textUser1;
                while ((textUser1 = reader.readLine()) != null) {
                    String user1 = getUser(textUser1, 1);
                    if (textUser1.contains(OperationType.TRANSFERRED.getOperation())) {
                        String user2 = getUser(textUser1, 2);
                        double money = getMoney(textUser1, OperationType.TRANSFERRED);
                        String time = getTime(textUser1);
                        String textUser2 = time + " " + user2 + " received " + money + " from " + user1;
                        userLogs.computeIfAbsent(user2, _ -> new ArrayList<>()).add(textUser2);
                    }
                    userLogs.computeIfAbsent(user1, _ -> new ArrayList<>()).add(textUser1);
                }
            }
        }
        return userLogs;
    }

    private static String createFinalBalance(String user, List<String> userLogs) {
        double balance = 0;
        int indexLastBalance = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
        String time = "[" + formatter.format(ZonedDateTime.now(ZoneId.systemDefault())) + "]";

        Optional<String> lastBalanceOp = userLogs.stream().filter(s -> s.contains(OperationType.INQUIRY.getOperation())).reduce((_, b) -> b);

        if (!lastBalanceOp.isEmpty()) {
            String lastBalance = lastBalanceOp.get();
            balance = getMoney(lastBalance, OperationType.INQUIRY);
            indexLastBalance = userLogs.indexOf(lastBalance);
        }

        for (int i = indexLastBalance; i < userLogs.size(); i++) {
            double moneyOperation;
            if (userLogs.get(i).contains(OperationType.TRANSFERRED.getOperation())) {
                moneyOperation = getMoney(userLogs.get(i), OperationType.TRANSFERRED);
                balance -= moneyOperation;
            } else if (userLogs.get(i).contains((OperationType.WITHDREW.getOperation()))) {
                moneyOperation = getMoney(userLogs.get(i), OperationType.WITHDREW);
                balance -= moneyOperation;
            } else if (userLogs.get(i).contains(OperationType.RECEIVED.getOperation())) {
                moneyOperation = getMoney(userLogs.get(i), OperationType.RECEIVED);
                balance += moneyOperation;
            }
        }

        return time + " " + user + " final balance " + balance;
    }

    private static double getMoney(String line, OperationType operation) {
        String money = null;
        int indexFirst;
        int indexLast;
        switch (operation) {
            case OperationType.TRANSFERRED: {
                indexFirst = line.indexOf(OperationType.TRANSFERRED.getOperation()) + 12;
                indexLast = line.indexOf(" to ");
                money = line.substring(indexFirst, indexLast);
                break;
            }
            case OperationType.RECEIVED: {
                indexFirst = line.indexOf(OperationType.RECEIVED.getOperation()) + 9;
                indexLast = line.indexOf(" from ");
                money = line.substring(indexFirst, indexLast);
                break;
            }
            case OperationType.INQUIRY: {
                indexFirst = line.indexOf(" ", line.indexOf(OperationType.INQUIRY.getOperation())) + 1;
                money = line.substring(indexFirst);
                break;
            }
            case OperationType.WITHDREW: {
                indexFirst = line.indexOf(" ", line.indexOf(OperationType.WITHDREW.getOperation())) + 1;
                money = line.substring(indexFirst);
                break;
            }
            default:
        }
        if (money == null) {
            return 0.0;
        }
        return Double.parseDouble(money);
    }

    private static String getUser(String line, int userNum) {
        String user = null;
        int indexFirst;
        int indexLast;
        switch (userNum) {
            case 1: {
                indexFirst = line.indexOf(']') + 2;
                indexLast = line.indexOf(' ', indexFirst);
                user = line.substring(indexFirst, indexLast);
                break;
            }
            case 2: {
                indexFirst = line.indexOf(" to ") + 4;
                user = line.substring(indexFirst);
                break;
            }
            default:
        }
        return user;
    }

    private static String getTime(String line) {
        String time;
        int indexFirst = 0;
        int indexLast = line.indexOf(']') + 1;
        time = line.substring(indexFirst, indexLast);
        return time;
    }
}