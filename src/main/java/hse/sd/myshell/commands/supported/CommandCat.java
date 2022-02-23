package hse.sd.myshell.commands.supported;

import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.CommandExternal;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class corresponding to the command cat.
 */
public class CommandCat implements AbstractCommand {
    private ArrayList<File> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private ExitCode exitCode = ExitCode.OK;
    private final Logger logger;

    public CommandCat(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandCat.class.getName())).getLogger();
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
            File f = new File(file);
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
     * Concatenates the content of static args into one line.
     * If there are no static args, then concatenates dynamic args.
     *
     * @return exitCode and result list.
     */
    @Override
    @NotNull
    public Result execute() {
        StringBuilder result = new StringBuilder();
        if (exitCode != ExitCode.OK || staticArgs.isEmpty() && dynamicArgs.isEmpty()) {
            exitCode = ExitCode.BAD_ARGS;
            return new Result(new ArrayList<>(), exitCode);
        }
        if (staticArgs.size() > 0) {
            for (File file : staticArgs) {
                try {
                    result.append(new String(Files.readAllBytes(file.toPath())) + "\n");
                } catch (IOException e) {
                    exitCode = ExitCode.UNKNOWN_PROBLEM;
                    logger.log(Level.WARNING, "Unknown problem with file: " + e.getMessage());
                }
            }
        } else {
            for (String str : dynamicArgs) {
                result.append(str + "\n");
            }
        }
        return new Result(new ArrayList<>(Collections.singleton(result.deleteCharAt(result.length() - 1).toString())), exitCode);
    }
}
