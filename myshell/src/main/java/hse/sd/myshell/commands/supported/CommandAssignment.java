package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.Result;

import java.util.ArrayList;
import java.util.logging.Logger;

public class CommandAssignment implements AbstractCommand {
    private ArrayList<String> staticArgs = null;
    private ArrayList<String> dinamicArgs = null;
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());
    private int exitcode = 0;

    public CommandAssignment(ArrayList<String> staticArgs, ArrayList<String> dinamicArgs) {
        validateStaticArgs(staticArgs);
        validateDinamicArgs(dinamicArgs);
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        if (args.size() != 2) {
            exitcode = 1;
            return;
        }
        staticArgs = args;
    }

    @Override
    public void validateDinamicArgs(ArrayList<String> args) {
        if (args != null) {
            exitcode = 1;
        }
    }

    @Override
    public Result execute() {
        if (staticArgs != null && dinamicArgs == null) {
            Environment.setVariableValue(staticArgs.get(0), staticArgs.get(1));
        }
        return new Result(null, exitcode);
    }
}
