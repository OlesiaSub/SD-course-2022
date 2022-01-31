package hse.sd.myshell;

import hse.sd.myshell.commands.CommandOutput;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Executor {

    public CommandOutput executeAll(String commandSequence) throws MyShellException {
        CommandParser parser = new CommandParser(commandSequence);
        ArrayList<String> commandArgs;
        CommandOutput commandOutput;
        commandArgs = parser.getNext();
        String commandName = commandArgs.get(0);
        commandArgs.remove(0);
        commandOutput = commandRedirect(commandName, commandArgs);
        return commandOutput;
    }

    private CommandOutput commandRedirect(String commandName, ArrayList<String> effectiveParameters) throws MyShellException {
        String className = "Command" + commandName;
        String outerClassName = "CommandOuter";
        String instanceMethodName = "execute";
        Class<?>[] formalParameters = {ArrayList.class};
        String packageName = getClass().getPackage().getName() + "commands";
        String supportedPackageName = packageName + ".supported";
        Class<?> clazz = null;
        CommandOutput output;
        try {
            clazz = Class.forName(supportedPackageName + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(packageName + "." + outerClassName);
            } catch (ClassNotFoundException e) {
                throw new MyShellException("Command with name" + commandName + "can not be executed");
            }
        }
        try {
            Method method = clazz.getMethod(instanceMethodName, formalParameters);
            Object newInstance = clazz.getDeclaredConstructor().newInstance();
            output = (CommandOutput) method.invoke(newInstance, effectiveParameters);
        } catch (Exception e) {
            throw new MyShellException("Execution of command" + commandName + "failed");
        }
        return output;
    }

    private static class CommandParser {

        private String currentRequest;

        CommandParser(String request) {
            currentRequest = request;
        }

        public ArrayList<String> getNext() {
            ArrayList<String> command = new ArrayList<>();
            boolean quote = false;
            StringBuilder currentToken = new StringBuilder();
            for (char symbol : currentRequest.toCharArray()) {
                // todo: я передаю аргументы все без кавычек, если надо - добавлю
                if (symbol == '\'' || symbol == '"') {
                    quote = !quote;
                    if (currentToken.length() > 0) command.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                if (quote) {
                    currentToken.append(symbol);
                    break;
                }
                if (symbol == ' ') {
                    if (currentToken.length() > 0) command.add(currentToken.toString());
                    currentToken = new StringBuilder();
                    break;
                }
            }
            return command;
        }
    }
}
