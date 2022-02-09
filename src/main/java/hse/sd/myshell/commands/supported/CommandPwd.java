package hse.sd.myshell.commands.supported;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Class corresponding to the command pwd.
 */
public class CommandPwd implements AbstractCommand {
    private final ArrayList<File> staticArgs = new ArrayList<>();
    private final ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandPwd.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    public CommandPwd(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
    }

    /**
     * Do nothing, because args are not important.
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
    }

    /**
     * Get current user dir.
     *
     * @return exitCode = EXIT and current user dir in result list.
     */
    @Override
    @NotNull
    public Result execute() {
        return new Result(new ArrayList<>(Collections.singleton(System.getProperty("user.dir"))), exitCode);
    }
}
