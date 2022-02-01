package hse.sd.myshell;

import hse.sd.myshell.commands.CommandOutput;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Executor {

    private final Logger LOG = Logger.getLogger(Executor.class.getName());

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
        commandName = commandName.substring(0, 1).toUpperCase(Locale.ROOT) + commandName.substring(1).toLowerCase(Locale.ROOT);
        String className = "Command" + commandName;
        String outerClassName = "CommandOuter";
        String instanceMethodName = "execute";
        Class<?>[] formalParameters = {ArrayList.class};
        String packageName = getClass().getPackage().getName() + ".commands";
        String supportedPackageName = packageName + ".supported";
        Class<?> clazz = null;
        CommandOutput output;
        try {
            clazz = Class.forName(supportedPackageName + "." + className);
        } catch (ClassNotFoundException e) {
            LOG.log(Level.INFO, "Required command \"" + commandName +
                    "\" is not supported and will be identified as an outer command");
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(packageName + "." + outerClassName);
            } catch (ClassNotFoundException e) {
                throw new MyShellException("Unable to execute outer command " + commandName);
            }
        }
        try {
            Method method = clazz.getMethod(instanceMethodName, formalParameters);
            Object newInstance = clazz.getDeclaredConstructor().newInstance();
            output = (CommandOutput) method.invoke(newInstance, effectiveParameters);
        } catch (NoSuchMethodException e) {
            throw new MyShellException("Can not find required method of the command " + commandName);
        } catch (InstantiationException e) {
            throw new MyShellException("Can not instantiate command " + commandName + "'s class");
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage() + '\n' + e.getCause());
            throw new MyShellException("Can not invoke required method of the command " + commandName);
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
                // todo: я передаю все аргументы без кавычек, если надо - добавлю
                if (symbol == '\'' || symbol == '"') {
                    quote = !quote;
                    if (currentToken.length() > 0) command.add(currentToken.toString());
                    currentToken = new StringBuilder();
                    continue;
                }
                if (quote) {
                    currentToken.append(symbol);
                    continue;
                }
                if (symbol == ' ') {
                    if (currentToken.length() > 0) command.add(currentToken.toString());
                    currentToken = new StringBuilder();
                    continue;
                }
                currentToken.append(symbol);
            }
            if (currentToken.length() > 0) command.add(currentToken.toString());
            return command;
        }
    }
}
