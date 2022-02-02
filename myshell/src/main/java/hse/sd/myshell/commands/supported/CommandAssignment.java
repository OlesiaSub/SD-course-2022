package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Logger;

public class CommandAssignment implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    public CommandAssignment(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        validateStaticArgs(staticArgs);
        validatedynamicArgs(dynamicArgs);
    }

    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        if (args.size() != 2) {
            exitCode = ExitCode.BAD_ARGS;
            return;
        }
        staticArgs = args;
    }

    @Override
    public void validatedynamicArgs(@NotNull ArrayList<String> args) {
        if (args.size() > 0) {
            exitCode = ExitCode.BAD_ARGS;
        }
    }

    @Override
    @NotNull
    public Result execute() {
        if (staticArgs.size() > 0 && dynamicArgs.size() == 0) {
            Environment.setVariableValue(staticArgs.get(0), staticArgs.get(1));
        }
        return new Result(new ArrayList<>(), exitCode);
    }
}
