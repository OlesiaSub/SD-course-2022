package hse.sd.myshell;

import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Executor {

    private final Logger LOG = Logger.getLogger(Executor.class.getName());

    public Result executeAll(@NotNull String commandSequence) throws MyShellException {
        CommandParser parser = new CommandParser(commandSequence);
        ArrayList<String> commandArgs;
        Result Result;
        commandArgs = parser.getNext();
        String commandName = commandArgs.get(0);
        commandArgs.remove(0);
        Result = commandRedirect(commandName, commandArgs, new ArrayList<>());
        return Result;
    }

    private Result commandRedirect(@NotNull String commandName, @NotNull ArrayList<String> staticArgs,
                                          @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        commandName = commandName.substring(0, 1).toUpperCase(Locale.ROOT) + commandName.substring(1).toLowerCase(Locale.ROOT);
        String className = "Command" + commandName;
        String outerClassName = "CommandOuter";
        String instanceMethodName = "execute";
        Class<?>[] formalParameters = {ArrayList.class, ArrayList.class};
        String packageName = getClass().getPackage().getName() + ".commands";
        String supportedPackageName = packageName + ".supported";
        Class<?> clazz = null;
        Result output;
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
            Object newInstance = clazz.getDeclaredConstructor(formalParameters).newInstance(staticArgs, dynamicArgs);
            Method method = clazz.getMethod(instanceMethodName);
            output = (Result) method.invoke(newInstance);
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

        CommandParser(@NotNull String request) {
            currentRequest = request;
        }

        public @NotNull ArrayList<String> getNext() {
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
                if (symbol == '=') {
                    if (currentToken.length() > 0) command.add(currentToken.toString());
                    currentToken = new StringBuilder();
                    command.add(0, "assignment");
                    continue;
                }
                currentToken.append(symbol);
            }
            if (currentToken.length() > 0) command.add(currentToken.toString());
            return command;
        }
    }
}
