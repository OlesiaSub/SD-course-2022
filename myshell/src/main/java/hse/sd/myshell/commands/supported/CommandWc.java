package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
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

public class CommandWc implements AbstractCommand {
    private ArrayList<File> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private int exitCode = 0;
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());

    public CommandWc(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        validateStaticArgs(staticArgs);
        validatedynamicArgs(dynamicArgs);
    }

    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        staticArgs = new ArrayList<>();
        for (String file : args) {
            File f = new File(file);
            if (!f.exists() || f.isDirectory()) {
                logger.log(Level.WARNING, "File does not exist: " + file);
                continue;
            }
            staticArgs.add(f);
        }
    }

    @Override
    public void validatedynamicArgs(@NotNull ArrayList<String> args) {
        dynamicArgs = args;
    }

    @Override
    @NotNull
    public Result execute() {
        ArrayList<String> result = new ArrayList<>();
        if (staticArgs.size() == 0 && dynamicArgs.size() == 0) {
            exitCode = 1;
            return new Result(new ArrayList<>(), exitCode);
        }
        if (staticArgs.size() > 0) {
            for (File file : staticArgs) {
                long lineCount = 0;
                long wordCount = 0;
                try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                    lineCount = stream.count();
                    wordCount = stream.map(c -> c.chars().filter(Character::isWhitespace).count()).reduce(0L, Long::sum);
                } catch (IOException e) {
                    exitCode = 2;
                }
                result.add(String.valueOf(lineCount) + ' ' + wordCount + ' ' + file.length() + ' ' + file.getPath());
            }
        } else {
            for (String string : dynamicArgs) {
                result.add("1 1 " + string.getBytes().length + "\n");
            }
        }
        return new Result(result, exitCode);
    }
}
