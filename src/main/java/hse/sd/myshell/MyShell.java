package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyShell {

    private static final Logger LOG = Logger.getLogger(MyShell.class.getName());

    /**
     * MyShell launcher, takes user's input and hands it over to [Executor]
     * Processes output and exit code of the executed command
     *
     * @param args will be ignored by the application
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Executor executor = new Executor();
        while (true) {
//            System.out.println(">>");
            String commandSequence = scanner.nextLine();
            if (commandSequence == null || commandSequence.isEmpty()) {
                continue;
            }
            Result output;
            try {
                output = executor.executeAll(commandSequence);
            } catch (MyShellException e) {
                LOG.log(Level.WARNING, e.getMessage());
                continue;
            }
            if (output.getExitCode() == ExitCode.EXIT) {
                LOG.log(Level.INFO, "Execution finished with exit code "
                        + output.getExitCode() + ", exiting the application");
                System.exit(0);
            }
            if (output.getExitCode() != ExitCode.OK) {
                LOG.log(Level.WARNING, "Execution finished with exit code " + output.getExitCode());
                continue;
            }
            LOG.log(Level.INFO, "Execution finished with exit code " + output.getExitCode());
            for (String res : output.getResult()) {
                System.out.println(res);
            }
            System.out.flush();
        }
    }
}
