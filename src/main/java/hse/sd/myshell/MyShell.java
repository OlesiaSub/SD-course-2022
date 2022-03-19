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
     * MyShell launcher, takes user's input, launches Preprocessing and transfers its result to Executor
     * Processes output and exit code of the last executed command
     * Finishes execution in case of either ctrl+D input or exit command
     */
    public static int run() {
        System.out.println("Staring MyShell...");
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        try {
            Executor executor = new Executor();
            Preprocessor preprocessor = new Preprocessor();
            while (true) {
                System.out.print(">> ");
                String commandSequence;
                try {
                    commandSequence = scanner.next();
                } catch (NoSuchElementException e) {
                    commandSequence = "exit";
                }
                if (commandSequence == null || commandSequence.isEmpty()) {
                    continue;
                }
                Result output;
                commandSequence = preprocessor.process(commandSequence);
                try {
                    output = executor.executeAll(commandSequence);
                } catch (MyShellException e) {
                    logger.log(Level.WARNING, e.getMessage());
                    continue;
                }
                if (output.getExitCode() == ExitCode.EXIT) {
                    System.out.println("Exiting MyShell...");
                    return 0;
                }
                for (String res : output.getResult()) {
                    System.out.println(res);
                }
            }
        } catch (MyShellException e) {
            logger.log(Level.WARNING, e.getMessage());
            System.out.println(e.getMessage());
        }
        return 1;
    }

    /**
     * Wraps run() to be launched from console
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        System.exit(run());
    }
}
