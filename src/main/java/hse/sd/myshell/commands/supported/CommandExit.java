package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Logger;

public class CommandExit implements AbstractCommand {
    private final ArrayList<String> staticArgs = new ArrayList<>();
    private final ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandExit.class.getName());
    private final ExitCode exitCode = ExitCode.EXIT;

    public CommandExit(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
    }

    @Override
    @NotNull
    public Result execute() {
        Environment.cleanVariables();
        return new Result(new ArrayList<>(), exitCode);
    }
}
