package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static core.DirectoryChecked.checkedDirectory;

public class CreatePaths {

    private static CreatePaths paths;
    private static Path directoryPath;
    private static Path transactionPath;
    private static boolean flag;

    public static CreatePaths getPaths() throws IOException {
        paths = new CreatePaths();
        return paths;
    }

    public static boolean getFlag() {
        return flag;
    }

    private CreatePaths() throws IOException { //Создает пути к директориям с заданными логами и логами на каждого юзера.
        Scanner sc = new Scanner(System.in);    //Если что-то не так, отправляет комментарий, что нужно исправить
        System.out.println("Enter the absolute path of the directory or \"exit\" if you want to exit: ");
        directoryPath = Path.of(sc.nextLine());

        while (!(Files.isDirectory(directoryPath) && directoryPath.isAbsolute() && checkedDirectory(directoryPath))) {
            if (directoryPath.toString().equals("exit")) {
                directoryPath = null;
                transactionPath = null;
                flag = false;
                return;
            }
            if (!(Files.isDirectory(directoryPath) && directoryPath.isAbsolute())) {
                System.out.println("You entered an invalid path. Enter the path again or \"exit\" if you want to exit: ");
            }
            else if (!checkedDirectory(directoryPath)) {
                System.out.println("There are no log files in this directory. Enter the path again or \"exit\" if you want to exit: ");
            }
            directoryPath = Path.of(sc.nextLine());
        }

        Path rootDirectoryPath = directoryPath.getParent();
        transactionPath = rootDirectoryPath.resolve("transactions_by_users");
        flag = true;
    }

    public static Path getDirectoryPath() {
        return directoryPath;
    }

    public static Path getTransactionPath() {
        return transactionPath;
    }
}
