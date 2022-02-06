package hse.sd.myshell.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Class for saving the result of commands execution.
 */
public class Result {
    private final ArrayList<String> result;
    private final ExitCode exitCode;

    public Result(@NotNull ArrayList<String> result, ExitCode exitCode) {
        this.result = result;
        this.exitCode = exitCode;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public ExitCode getExitCode() {
        return exitCode;
    }
}
