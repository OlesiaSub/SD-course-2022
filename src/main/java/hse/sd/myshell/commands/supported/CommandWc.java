package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Class corresponding to the command wc.
 */
public class CommandWc implements AbstractCommand {
    private ArrayList<File> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private ExitCode exitCode = ExitCode.OK;
    private final Logger logger = Logger.getLogger(CommandWc.class.getName());

    public CommandWc(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
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
        ArrayList<String> result = new ArrayList<>();
        if (staticArgs.size() > 0) {
            for (File file : staticArgs) {
                long lineCount = 0;
                long wordCount = 0;
                try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8);
                     Stream<String> wcStream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                    lineCount = stream.map(c -> c.chars().filter(x -> x == '\n').count()).reduce(0L, Long::sum) + 1;
                    wordCount = wcStream.map(c -> c.chars().filter(Character::isWhitespace).count()).reduce(0L, Long::sum) + 1;
                } catch (IOException e) {
                    exitCode = ExitCode.UNKNOWN_PROBLEM;
                    logger.log(Level.WARNING, "Unknown problem with file: " + e.getMessage());
                }
                result.add(String.valueOf(lineCount) + ' ' + wordCount + ' ' + file.length() + ' ' + file.getPath());
            }
        } else {
            for (String string : dynamicArgs) {
                result.add(string.lines().count() + " " + (1 + string.chars().filter(Character::isWhitespace).count()) + " " + string.getBytes().length + "\n");
            }
        }
        return new Result(result, exitCode);
    }
}
