package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class CommandPwd implements AbstractCommand {
    private final ArrayList<File> staticArgs = null;
    private final ArrayList<String> dinamicArgs = null;
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());
    private final int exitcode = 0;

    public CommandPwd(ArrayList<String> staticArgs, ArrayList<String> dinamicArgs) {
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
    }

    @Override
    public void validateDinamicArgs(ArrayList<String> args) {
    }

    @Override
    public Result execute() {
        return new Result(new ArrayList<>(Collections.singleton(System.getProperty("user.dir"))), exitcode);
    }
}
