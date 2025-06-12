package core;

public class GetItems {
    static double getMoney(String line, OperationType operation) {
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

    static String getUser(String line, int userNum) {
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

    static String getTime(String line) {
        String time;
        int indexFirst = 0;
        int indexLast = line.indexOf(']') + 1;
        time = line.substring(indexFirst, indexLast);
        return time;
    }


}
