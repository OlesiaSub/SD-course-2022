package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandCat implements AbstractCommand {
    private ArrayList<File> staticArgs = null;
    private ArrayList<String> dinamicArgs = null;
    private int exitcode = 0;
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());

    public CommandCat(ArrayList<String> staticArgs, ArrayList<String> dinamicArgs) {
        validateStaticArgs(staticArgs);
        validateDinamicArgs(dinamicArgs);
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        if (args == null || args.size() == 0) return;
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
        if (args == null || args.size() == 0) return;
        dinamicArgs = args;
    }

    @Override
    public Result execute() {
        StringBuilder result = new StringBuilder();
        if (staticArgs != null) {
            for (File file : staticArgs) {
//              TODO: change to try with resources
                try {
                    result.append(new String(Files.readAllBytes(file.toPath())));
                    result.append('\n');
                } catch (IOException e) {
                    exitcode = 2;
                }
            }
        } else if (dinamicArgs != null) {
            for (String string : dinamicArgs) {
                result.append(string);
                result.append('\n');
            }
        } else {
            exitcode = 1;
            return new Result(new ArrayList<>(), exitcode);
        }
        return new Result(new ArrayList<>(Collections.singleton(result.toString())), exitcode);
    }
}
