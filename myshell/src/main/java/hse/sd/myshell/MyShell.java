package hse.sd.myshell;

import hse.sd.myshell.commands.CommandOutput;

import java.util.Scanner;

public class MyShell {
    public static void main(String[] args) throws MyShellException {
        Scanner scanner = new Scanner(System.in);
        Executor executor = new Executor();
        while (true) {
            System.out.print(">>> ");
            String commandSequence = scanner.nextLine();
            CommandOutput output = executor.executeAll(commandSequence);
            if (output.getExitCode() != 0) {
                System.out.println("Execution finished with exit code " + output.getExitCode());
                continue;
            }
            System.out.println("output: " + output.getOutput());
        }
    }
}
