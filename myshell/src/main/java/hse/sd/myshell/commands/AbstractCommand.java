package hse.sd.myshell.commands;

import java.util.ArrayList;

public interface AbstractCommand {
    CommandOutput execute(ArrayList<String> staticArgs, ArrayList<String> dynamicArgs);
}
