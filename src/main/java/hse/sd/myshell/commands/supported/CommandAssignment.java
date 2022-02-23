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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Class corresponding to the assignment command.
 */
public class CommandAssignment implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private final ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger;
    private ExitCode exitCode = ExitCode.OK;
    private final Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");

    public CommandAssignment(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandAssignment.class.getName())).getLogger();
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    /**
     * Check args, save if correct, update exitCode if incorrect. Should be only 2 static args (variable and value)
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        if (args.size() != 2) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Not 2 static arguments in Assignment: " + String.join(", ", args));
            return;
        }
        String variable = args.get(0);
        if (!pattern.matcher(variable).matches() || Character.isDigit(variable.charAt(0))) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Variable format is not supported.");
            return;
        }
        staticArgs = args;
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
    }

    /**
     * Update variable in environment.
     *
     * @return exitCode and result list.
     */
    @Override
    @NotNull
    public Result execute() {
        if (staticArgs.size() > 0) {
            Environment.setVariableValue(staticArgs.get(0), staticArgs.get(1));
        }
        return new Result(new ArrayList<>(), exitCode);
    }
}
