package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CommandWc implements AbstractCommand {
    private ArrayList<File> staticArgs = null;
    private ArrayList<String> dinamicArgs = null;
    private int exitcode = 0;
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());

    public CommandWc(ArrayList<String> staticArgs, ArrayList<String> dinamicArgs) {
        validateStaticArgs(staticArgs);
        validateDinamicArgs(dinamicArgs);
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        staticArgs = new ArrayList<>();
        for (String file : args) {
            File f = new File(file);
            if (!f.exists() || f.isDirectory()) {
                logger.log(Level.WARNING, "File is not exist: " + file);
                continue;
            }
            staticArgs.add(f);
        }
    }

    @Override
    public void validateDinamicArgs(ArrayList<String> args) {
        dinamicArgs = args;
    }

    @Override
    public Result execute() {
        ArrayList<String> result = new ArrayList<>();
        if (staticArgs != null) {
            for (File file : staticArgs) {
                long lineCount = 0;
                long wordCount = 0;
                try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                    lineCount = stream.count();
                    wordCount = stream.filter(Character::isSpaceChar).count();
                } catch (IOException e) {
                    exitcode = 2;
                }
                result.add(String.valueOf(lineCount) + ' ' + wordCount + ' ' + file.length() + ' ' + file.getPath());
            }
        } else if (dinamicArgs != null) {
            for (String string : dinamicArgs) {
                result.add("1 1 " + string.getBytes().length + "\n");
            }
        } else {
            exitcode = 1;
        }
        return new Result(result, exitcode);
    }
}
