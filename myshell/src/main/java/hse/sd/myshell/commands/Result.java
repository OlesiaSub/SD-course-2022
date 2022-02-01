package hse.sd.myshell.commands;

import java.util.ArrayList;

public class Result {
    public ArrayList<String> getResult() {
        return result;
    }

    public int getExitcode() {
        return exitcode;
    }

    ArrayList<String> result;
        int exitcode;

        public Result(ArrayList<String> reslult, int exitcode) {
            this.result = reslult;
            this.exitcode = exitcode;
        }
}
