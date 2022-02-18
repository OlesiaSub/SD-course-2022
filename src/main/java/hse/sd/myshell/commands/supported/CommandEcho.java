package hse.sd.myshell.commands.supported;

import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.CommandExternal;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class corresponding to the command echo.
 */
public class CommandEcho implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger;
    private ExitCode exitCode = ExitCode.OK;

    public CommandEcho(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandEcho.class.getName())).getLogger();
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    /**
     * Check args, save if existed.
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        staticArgs = args;
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
        dynamicArgs = args;
    }

    /**
     * Args should be.
     * Concatenates static args into one line.
     * If there are no static args, then concatenates dynamic args.
     *
     * @return exitCode and result list.
     */
    @Override
    @NotNull
    public Result execute() {
        StringBuilder result = new StringBuilder();
        if ((staticArgs.size() > 0 && dynamicArgs.size() > 0)
                || (staticArgs.size() == 0 && dynamicArgs.size() == 0)) {
            return new Result(new ArrayList<>(), exitCode);
        }
        if (staticArgs.size() > 0) {
            for (String str : staticArgs) {
                result.append(str + ' ');
            }
        } else {
            for (String str : dynamicArgs) {
                result.append(str + ' ');
            }
        }
        return new Result(new ArrayList<>(Collections.singleton(result.deleteCharAt(result.length() - 1).toString())), exitCode);
    }
}
