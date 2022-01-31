package hse.sd.myshell;

import java.util.Scanner;

public class MyShell {
    public static void main(String[] args) throws MyShellException {
        Scanner scanner = new Scanner(System.in);
        Executor executor = new Executor();
        while (true) {
            String commandSequence = scanner.nextLine();
            executor.executeAll(commandSequence);
            System.out.println("Your string: " + commandSequence);
        }
    }
}
