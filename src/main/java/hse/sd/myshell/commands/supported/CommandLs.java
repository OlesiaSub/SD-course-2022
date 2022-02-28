package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class corresponding to the command ls.
 */
public class CommandLs implements AbstractCommand {
    private String rawPathStringArg;
    private Path pathArg;
    private ExitCode exitCode = ExitCode.OK;
    private final Logger logger;

    public CommandLs(
            @NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandCd.class.getName())).getLogger();
        validateStaticArgs(staticArgs);
    }

    /**
     * Check args, save if correct, update exitCode if incorrect.
     * Static arguments must be either single existing file / directory or empty list.
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
        rawPathStringArg = args.get(0);
        pathArg = Environment.resolvePathInCurrentDirectory(rawPathStringArg);
        if (pathArg == null || !Files.exists(pathArg)) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "File does not exist: " + args.get(0));
        }
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
    }

    /**
     * Prints current directory to static arg.
     * If static arg is a directory, prints relative names of files in it (non-recursive).
     * If static arg is not a directory, prints static arg.
     * If there is no static arg, then prints relative names of files in the current directory (non-recursive).
     * <p>
     * Special links to the directory itself and the directory's parent directory won't be printed.
     *
     * @return exitCode and result list.
     */
    @Override
    @NotNull
    public Result execute() {
        if (exitCode != ExitCode.OK) {
            return new Result(new ArrayList<>(), exitCode);
        }
        pathArg = Objects.requireNonNullElse(pathArg, Environment.getCurrentDirectoryPath());
        if (!Files.isDirectory(pathArg)) {
            return new Result(new ArrayList<>(Collections.singleton(rawPathStringArg)), exitCode);
        }
        try {
            String result = Files.list(pathArg)
                    .map(path -> pathArg.relativize(path.toAbsolutePath()).toString()) // getAbsolutePath
                    .sorted()
                    .collect(Collectors.joining("  "));
            return new Result(new ArrayList<>(Collections.singleton(result)), exitCode);
        } catch (IOException e) {
            logger.log(Level.WARNING,
                       "Failed to list files in directory \"" + pathArg.toString() + "\" due to IOException: " + e.getMessage());
            exitCode = ExitCode.UNKNOWN_PROBLEM;
            return new Result(new ArrayList<>(), exitCode);
        }
    }
}
