package hse.sd.myshell.commands;

import java.util.ArrayList;

public class CommandOutput {
    private final int exitCode;
    private final ArrayList<String> output;

    CommandOutput(int exitCode, ArrayList<String> output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public ArrayList<String> getOutput() {
        return output;
    }
}
