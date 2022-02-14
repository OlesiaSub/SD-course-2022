package hse.sd.myshell.commands;

import hse.sd.myshell.Environment;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for processing external commands
 */
public class CommandExternal implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandExternal.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    /**
     * Constructor, validates arguments
     *
     * @param staticArgs  static arguments
     * @param dynamicArgs dynamic arguments
     */
    public CommandExternal(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    /**
     * @see AbstractCommand
     */
    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        staticArgs = args;
    }

    /**
     * @see AbstractCommand
     */
    @Override
    public void validateDynamicArgs(ArrayList<String> args) {
        dynamicArgs = args;
    }

    /**
     * Builds a process for the external command to be executed in
     *
     * @return Result of the execution
     * @see Result
     */
    @Override
    public Result execute() {
        String output = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(staticArgs);
            builder.directory(new File(System.getProperty("user.dir")));
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.environment().putAll(Environment.getEnvironment());
            Process process = builder.start();
            output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.waitFor() != 0) {
                logger.log(Level.WARNING, "Exit code " + process.exitValue());
                exitCode = ExitCode.UNKNOWN_PROBLEM;
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.WARNING, e.getMessage());
            exitCode = ExitCode.UNKNOWN_PROBLEM;
        }
        return new Result(new ArrayList<>(Collections.singleton(output)), exitCode);
    }
}
