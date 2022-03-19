package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class corresponding to the command cd.
 */
public class CommandCd implements AbstractCommand {
    private Path newDirectoryPathArg;
    private ExitCode exitCode = ExitCode.OK;
    private final Logger logger;

    public CommandCd(
            @NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandCd.class.getName())).getLogger();
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    /**
     * Check args, save if correct, update exitCode if incorrect.
     * Static arguments must be either single existing directory or empty list.
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        if (args.isEmpty()) return;
        if (args.size() > 1) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Too many static arguments: 0 or 1 allowed");
            return;
        }
        newDirectoryPathArg = Environment.resolvePathInCurrentDirectory(args.get(0));
        if (newDirectoryPathArg == null) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Bad path: " + args.get(0));
            return;
        }
        if (!Files.isDirectory(newDirectoryPathArg)) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Directory does not exist: " + args.get(0));
        }
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
    }

    /**
     * Changes current directory to static arg.
     * If there is no static arg, then changes to user home.
     *
     * @return exitCode and empty result list.
     */
    @Override
    @NotNull
    public Result execute() {
        if (exitCode == ExitCode.OK) {
            Environment.setCurrentDirectoryPath(
                    Objects.requireNonNullElse(newDirectoryPathArg, Path.of(System.getProperty("user.home"))));
        }
        return new Result(new ArrayList<>(), exitCode);
    }
}
