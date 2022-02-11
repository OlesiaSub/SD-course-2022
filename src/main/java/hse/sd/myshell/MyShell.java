package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyShell {

    private static final Logger logger = Logger.getLogger(MyShell.class.getName());

    /**
     * MyShell launcher, takes user's input, launches Preprocessing and transfers its result to Executor
     * Processes output and exit code of the last executed command
     *
     * @param args will be ignored by the application
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Executor executor = new Executor();
        Preprocessor preprocessor = new Preprocessor();
        while (true) {
            String commandSequence = scanner.nextLine();
            Result output;
            commandSequence = preprocessor.process(commandSequence);
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
        }
    }
}
