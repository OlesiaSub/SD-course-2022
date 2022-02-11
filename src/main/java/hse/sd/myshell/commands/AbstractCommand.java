package hse.sd.myshell.commands;

import java.util.ArrayList;

/**
 * Interface for commands.
 */
public interface AbstractCommand {
    /**
     * Check args, save if correct, update exitcode if incorrect.
     *
     * @param args a sequence of args for validating.
     */
    void validateStaticArgs(ArrayList<String> args);

    void validateDynamicArgs(ArrayList<String> args);

    /**
     * Execute command.
     */
    Result execute();
}
