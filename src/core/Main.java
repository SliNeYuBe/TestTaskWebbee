package core;

import java.io.IOException;
import java.nio.file.Path;

import static core.WriteLogs.writeLogs;

public class Main {
    public static void main(String[] args) throws IOException {
        CreatePaths paths;
        boolean flag;
        while (true) {
            paths = CreatePaths.getPaths();
            flag = CreatePaths.getFlag();
            if (!flag) {break;}
            Path directoryPath = paths.getDirectoryPath();
            Path transactionPath = paths.getTransactionPath();
            writeLogs(directoryPath, transactionPath);
            System.out.println("You create logs!");
        }
    }
}