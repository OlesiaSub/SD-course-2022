package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyShell {

    private static final Logger LOG = Logger.getLogger(MyShell.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Executor executor = new Executor();
        while (true) {
            String commandSequence = scanner.nextLine();
            Result output;
            try {
                output = executor.executeAll(commandSequence);
            } catch (MyShellException e) {
                LOG.log(Level.WARNING, e.getMessage());
                continue;
            }
            if (output.getExitCode() != ExitCode.OK) {
                LOG.log(Level.WARNING, "Execution finished with exit code " + output.getExitCode());
                continue;
            }
            LOG.log(Level.INFO, "Execution finished with exit code " + output.getExitCode());
            for (String res : output.getResult()) {
                System.out.println(res);
            }
        }
    }
}
