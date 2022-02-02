package hse.sd.myshell.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Result {
    private final ArrayList<String> result;
    private final int exitCode;

    public Result(@NotNull ArrayList<String> reslult, int exitCode) {
        this.result = reslult;
        this.exitCode = exitCode;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public int getExitCode() {
        return exitCode;
    }
}
