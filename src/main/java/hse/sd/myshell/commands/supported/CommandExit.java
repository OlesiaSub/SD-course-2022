package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.CommandExternal;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class corresponding to the command exit.
 */
public class CommandExit implements AbstractCommand {
    private final ArrayList<String> staticArgs = new ArrayList<>();
    private final ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger;
    private final ExitCode exitCode = ExitCode.EXIT;

    public CommandExit(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandExit.class.getName())).getLogger();
    }

    /**
     * Do nothing, because args are not important.
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
    }

    /**
     * Clean the environment.
     *
     * @return exitCode = EXIT and empty result list.
     */
    @Override
    @NotNull
    public Result execute() {
        Environment.cleanVariables();
        return new Result(new ArrayList<>(), exitCode);
    }
}
