package hse.sd.myshell.commands;

import java.util.ArrayList;

public interface AbstractCommand {
    void validateStaticArgs(ArrayList<String> args);

    void validateDynamicArgs(ArrayList<String> args);

    Result execute();
}
