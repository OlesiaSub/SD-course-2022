package hse.sd.myshell.commands;

import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.CommandOutput;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandOuter implements AbstractCommand {
    @Override
    public CommandOutput execute(ArrayList<String> params) {
        return new CommandOutput(0, new ArrayList<String>(Arrays.asList("outer", "command", "execution")));
    }
}
