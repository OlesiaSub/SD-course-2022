package hse.sd.myshell.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Result {
    ArrayList<String> result;
    int exitcode;

    public Result(@NotNull ArrayList<String> reslult, int exitcode) {
        this.result = reslult;
        this.exitcode = exitcode;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public int getExitcode() {
        return exitcode;
    }
}
