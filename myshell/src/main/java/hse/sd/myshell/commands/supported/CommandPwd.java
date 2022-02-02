package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class CommandPwd implements AbstractCommand {
    private final ArrayList<File> staticArgs = new ArrayList<>();
    private final ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandCat.class.getName());
    private final int exitcode = 0;

    public CommandPwd(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
    }

    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
    }

    @Override
    public void validatedynamicArgs(@NotNull ArrayList<String> args) {
    }

    @Override
    @NotNull
    public Result execute() {
        return new Result(new ArrayList<>(Collections.singleton(System.getProperty("user.dir"))), exitcode);
    }
}
