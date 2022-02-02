package hse.sd.myshell.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Result {
    private final ArrayList<String> result;
    private final ExitCode exitCode;

    public Result(@NotNull ArrayList<String> reslult, ExitCode exitCode) {
        this.result = reslult;
        this.exitCode = exitCode;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public ExitCode getExitCode() {
        return exitCode;
    }
}
