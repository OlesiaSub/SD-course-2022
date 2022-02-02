package hse.sd.myshell.commands;

import java.util.ArrayList;

public interface AbstractCommand {
    void validateStaticArgs(ArrayList<String> args);

    void validatedynamicArgs(ArrayList<String> args);

    Result execute();
}
