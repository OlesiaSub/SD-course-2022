package hse.sd.myshell.commands;

import java.util.ArrayList;
import java.util.logging.Logger;

public interface AbstractCommand {
    void validateStaticArgs(ArrayList<String> staticArgs);
    void validateDinamicArgs(ArrayList<String> dinamicArgs);
    Result execute();

    class Result{
        ArrayList<String> reslult;
        int exitcode;

        public Result(ArrayList<String> reslult, int exitcode) {
            this.reslult = reslult;
            this.exitcode = exitcode;
        }
    }
}
