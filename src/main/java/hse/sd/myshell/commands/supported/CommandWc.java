package hse.sd.myshell.commands.supported;

import hse.sd.myshell.Environment;
import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.Files.readString;

/**
 * Class corresponding to the command wc.
 */
public class CommandWc implements AbstractCommand {
    private ArrayList<File> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private ExitCode exitCode = ExitCode.OK;
    private final Logger logger;

    public CommandWc(
            @NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandWc.class.getName())).getLogger();
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    /**
     * Check args, save if correct, update exitCode if incorrect. All static args should be existing files.
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        staticArgs = new ArrayList<>();
        for (String file : args) {
            Path path = Environment.resolvePathInCurrentDirectory(file);
            if (path == null) {
                logger.log(Level.WARNING, "Invalid path: " + file);
                exitCode = ExitCode.BAD_ARGS;
                return;
            }
            File f = path.toFile();
            if (!f.exists() || f.isDirectory()) {
                logger.log(Level.WARNING, "File does not exist: " + file);
                exitCode = ExitCode.BAD_ARGS;
                return;
            }
            staticArgs.add(f);
        }
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
        if (args.size() > 1) {
            exitCode = ExitCode.BAD_ARGS;
            return;
        }
        dynamicArgs = args;
    }

    /**
     * Concatenates the number of lines, words and bytes of static args into one line.
     * If there are no static args, then concatenates the number of lines, words and bytes of dynamic args.
     *
     * @return exitCode and result list.
     */
    @Override
    @NotNull
    public Result execute() {
        if (exitCode != ExitCode.OK || staticArgs.isEmpty() && dynamicArgs.isEmpty()) {
            exitCode = ExitCode.BAD_ARGS;
            return new Result(new ArrayList<>(), exitCode);
        }
        StringBuilder result = new StringBuilder();
        if (staticArgs.size() > 0) {
            for (File file : staticArgs) {
                try {
                    String content = readString(file.toPath());
                    result.append(content.lines().count() + " " + content.replaceAll("\\s+", " ").trim().split(
                            " ").length + " " + content.getBytes().length + " " + file.getPath() + "\n");
                } catch (IOException e) {
                    exitCode = ExitCode.UNKNOWN_PROBLEM;
                    logger.log(Level.WARNING, "Unknown problem with file: " + e.getMessage());
                }
            }
        } else {
            for (String string : dynamicArgs) {
                result.append(string.lines().count() + " " + string.replaceAll("\\s+", " ").trim().split(
                        " ").length + " " + string.getBytes().length + "\n");
            }
        }
        return new Result(new ArrayList<>(Collections.singleton(result.toString().trim() + "\n")), exitCode);
    }
}
