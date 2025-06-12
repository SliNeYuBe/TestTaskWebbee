package core;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static core.GetItems.getMoney;

public class CreateItems {
    static String createFinalBalance(String user, List<String> userLogs) {
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

    static String createLog(String time, String user1, String user2, double money) { //Создает строчку с определенным действием юзера
        return time + " " + user2 + " " + OperationType.RECEIVED.getOperation() + " " + money + " from " + user1;
    }

    static String createLog(String time, String user1, double balance) { //Перегрузка создание лога
        return time + " " + user1 + " " + OperationType.FINAL_BALANCE.getOperation() + " " + balance;
    }

    static String createLog(String time, String user1, OperationType operation) { //Перегрузка создание лога
        if (operation == OperationType.ERROR_NO_INQUIRY_BALANCE) {
            return time + " " + user1 + " does not have a balance inquiry operation to find out the exact balance";
        }
        else {
            return time + " " + user1 + " current balance does not match the inquiry balance";
        }
    }

    static String createTimeNow() { //Создает текущую дату и время для лога
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
        String time = "[" + formatter.format(ZonedDateTime.now(ZoneId.systemDefault())) + "]";
        return time;
    }
}
