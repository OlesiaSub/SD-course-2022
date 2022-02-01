package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class CommandEcho implements AbstractCommand {
    private ArrayList<String> staticArgs = null;
    private ArrayList<String> dinamicArgs = null;
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());
    private int exitcode = 0;

    public CommandEcho(ArrayList<String> staticArgs, ArrayList<String> dinamicArgs) {
        validateStaticArgs(staticArgs);
        validateDinamicArgs(dinamicArgs);
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        if (args != null && args.size() > 0) {
            staticArgs = args;
        }
    }

    @Override
    public void validateDinamicArgs(ArrayList<String> args) {
        if (args != null && args.size() > 0) {
            dinamicArgs = args;
        }
    }

    @Override
    public Result execute() {
        StringBuilder result = new StringBuilder();
        if (staticArgs != null && dinamicArgs != null) {
            exitcode = 1;
            return new Result(new ArrayList<>(), exitcode);
        }
        if (staticArgs != null) {
            for (String str : staticArgs) {
                result.append(str + ' ');
            }
        } else if (dinamicArgs != null) {
            for (String str : dinamicArgs) {
                result.append(str + ' ');
            }
        } else {
            exitcode = 1;
            return new Result(new ArrayList<>(), exitcode);
        }
        return new Result(new ArrayList<>(Collections.singleton(result.deleteCharAt(result.length() - 1).toString())), exitcode);
    }
}
