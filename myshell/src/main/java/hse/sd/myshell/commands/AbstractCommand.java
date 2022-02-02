package hse.sd.myshell.commands;

import java.util.ArrayList;

public interface AbstractCommand {
    void validateStaticArgs(ArrayList<String> staticArgs);

    void validatedynamicArgs(ArrayList<String> dynamicArgs);

    Result execute();
}

