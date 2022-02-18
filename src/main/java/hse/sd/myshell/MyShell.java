package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyShell {

    private static final Logger logger;

    static {
        try {
            logger = (new LoggerWithHandler(MyShell.class.getName())).getLogger();
        } catch (MyShellException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * MyShell launcher, takes user's input and hands it over to [Executor]
     * Processes output and exit code of the executed command
     *
     * @param args will be ignored by the application
     */
    public static void main(String[] args) {
        try {
            System.out.println("Staring MyShell...");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            Executor executor = new Executor();
            while (true) {
                System.out.print(">> ");
                String commandSequence = "";
                try {
                    commandSequence = scanner.next();
                } catch (NoSuchElementException e) {
                    commandSequence = "exit";
                }
                if (commandSequence == null || commandSequence.isEmpty()) {
                    continue;
                }
                Result output;
                try {
                    output = executor.executeAll(commandSequence);
                } catch (MyShellException e) {
                    logger.log(Level.WARNING, e.getMessage());
                    continue;
                }
                if (output.getExitCode() == ExitCode.EXIT) {
                    logger.log(Level.INFO, "Execution finished with exit code "
                            + output.getExitCode() + ", exiting the application");
                    System.exit(0);
                }
                if (output.getExitCode() != ExitCode.OK) {
                    logger.log(Level.WARNING, "Execution finished with exit code " + output.getExitCode());
                    continue;
                }
                logger.log(Level.INFO, "Execution finished with exit code " + output.getExitCode());
                for (String res : output.getResult()) {
                    System.out.println(res);
                }
                System.out.println();
                System.out.flush();
            }
        } catch (MyShellException e) {
            System.out.println(e.getMessage());
        }
    }
}
