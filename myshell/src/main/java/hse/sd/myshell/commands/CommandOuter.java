package hse.sd.myshell.commands;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.CommandOutput;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandOuter implements AbstractCommand {
    @Override
    public CommandOutput execute(ArrayList<String> staticArgs, ArrayList<String> dynamicArgs) {
        return new CommandOutput(0, new ArrayList<>(Arrays.asList("outer", "command", "execution")));
    }
}
