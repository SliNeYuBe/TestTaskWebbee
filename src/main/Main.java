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
        System.out.println("Enter the absolute path of the directory or exit if you want to exit: ");
        Path directoryPath = Path.of(sc.nextLine());

        while (!(Files.isDirectory(directoryPath) && directoryPath.isAbsolute())) {
            if (directoryPath.toString().equals("exit")) {
                return;
            }
            System.out.println("You entered an invalid path. Enter it again or exit if you want to exit: ");
            directoryPath = Path.of(sc.nextLine());
        }

        Path rootDirectoryPath = directoryPath.getParent();
        Path transactionPath = rootDirectoryPath.resolve("transaction_by_users");
        clearTransactionDirectory(transactionPath);
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

    private static String createFinalBalance(String user, List<String> userLogs) {
        double balance;
        int indexLastBalance;
        String time = createTimeNow();

        List<String> inquiryBalanceList = userLogs.stream().filter(s -> s.contains(OperationType.INQUIRY.getOperation())).collect(Collectors.toList());

        if (inquiryBalanceList.size() <= 0) {
            return createLog(time, user, OperationType.ERROR_NO_INQUIRY_BALANCE);
        }
        else if (inquiryBalanceList.size() == 1) {
            indexLastBalance = userLogs.indexOf(inquiryBalanceList.get(inquiryBalanceList.size() - 1));
        }
        else {
            indexLastBalance = userLogs.indexOf(inquiryBalanceList.get(0));
        }

        balance = getMoney(userLogs.get(indexLastBalance), OperationType.INQUIRY);


        for (int i = indexLastBalance; i < userLogs.size(); i++) {
            double moneyOperation;
            if (userLogs.get(i).contains(OperationType.INQUIRY.getOperation())) {
                if (Math.abs(balance - getMoney(userLogs.get(i), OperationType.INQUIRY)) > 0.000001) {
                    return createLog(time, user, OperationType.ERROR_NO_EQUALS_BALANCE_AND_INQUIRY_BALANCE);
                }
            }

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

        return createLog(time, user, balance);
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

    private static String createLog(String time, String user1, String user2, double money) {
        return time + " " + user2 + " " + OperationType.RECEIVED.getOperation() + " " + money + " from " + user1;
    }

    private static String createLog(String time, String user1, double balance) {
        return time + " " + user1 + " " + OperationType.FINAL_BALANCE.getOperation() + " " + balance;
    }

    private static String createLog(String time, String user1, OperationType operation) {
        if (operation == OperationType.ERROR_NO_INQUIRY_BALANCE) {
            return time + " " + user1 + " does not have a balance inquiry operation to find out the exact balance";
        }
        else {
            return time + " " + user1 + " current balance does not match the inquiry balance";
        }
    }
    
    private static String createTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
        String time = "[" + formatter.format(ZonedDateTime.now(ZoneId.systemDefault())) + "]";
        return time;
    }

    private static void clearTransactionDirectory(Path transactionPath) throws IOException {
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