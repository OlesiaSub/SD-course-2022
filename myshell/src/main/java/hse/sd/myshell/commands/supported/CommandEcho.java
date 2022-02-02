package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandEcho implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    public CommandEcho(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        if (args.size() > 0) {
            staticArgs = args;
        }
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
        if (args.size() > 0) {
            dynamicArgs = args;
        }
    }

    @Override
    @NotNull
    public Result execute() {
        StringBuilder result = new StringBuilder();
        if ((staticArgs.size() > 0 && dynamicArgs.size() > 0)
                || (staticArgs.size() == 0 && dynamicArgs.size() == 0)) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Bad arguments in echo (static: " + String.join(", ", staticArgs) + ", dynamic: " + String.join(", ", staticArgs) + ")");
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
