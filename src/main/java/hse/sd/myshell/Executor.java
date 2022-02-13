package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for commands execution
 */
public class Executor {

    private final Logger logger = Logger.getLogger(Executor.class.getName());

    /**
     * Parses user input, transfers it to the corresponding class and executes it there
     *
     * @param commandSequence a sequence of commands entered by user
     * @return Result, which contains result and exit code of the last executed command
     * @throws MyShellException in case of any mistakes that were not handled by the application
     */
    public Result executeAll(@NotNull String commandSequence) throws MyShellException {
        CommandParser parser = new CommandParser(commandSequence);
        ArrayList<String> commandArgs;
        Result result;
        commandArgs = parser.getNext();
        if (commandArgs.isEmpty()) {
            return new Result(new ArrayList<>(), ExitCode.BAD_ARGS);
        }
        String commandName = commandArgs.get(0);
        commandArgs.remove(0);
        result = commandRedirect(commandName, commandArgs, new ArrayList<>());
        return result;
    }

    private Result commandRedirect(@NotNull String commandName, @NotNull ArrayList<String> staticArgs,
                                   @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        String supportedCommandName = commandName;
        if (commandName.equals(commandName.toLowerCase())) {
            supportedCommandName = commandName.substring(0, 1).toUpperCase(Locale.ROOT)
                    + commandName.substring(1).toLowerCase(Locale.ROOT);
        }
        String className = "Command" + supportedCommandName;
        String outerClassName = "CommandExternal";
        String instanceMethodName = "execute";
        Class<?>[] formalParameters = {ArrayList.class, ArrayList.class};
        String packageName = getClass().getPackage().getName() + ".commands";
        String supportedPackageName = packageName + ".supported";
        Class<?> clazz = null;
        Result output;
        try {
            clazz = Class.forName(supportedPackageName + "." + className);
        } catch (ClassNotFoundException e) {
            logger.log(Level.INFO, "Required command \"" + commandName +
                    "\" is not supported and will be identified as an outer command");
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(packageName + "." + outerClassName);
                staticArgs.add(0, commandName);
            } catch (ClassNotFoundException e) {
                throw new MyShellException("Unable to execute outer command " + commandName);
            }
        }
        try {
            Object newInstance = clazz.getDeclaredConstructor(formalParameters).newInstance(staticArgs, dynamicArgs);
            Method method = clazz.getMethod(instanceMethodName);
            output = (Result) method.invoke(newInstance);
            if (output.getResult().size() <= 1) {
                output = (Result) method.invoke(newInstance);
            }
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

    /**
     * Class for parsing user input into sequence of commands
     */
    private static class CommandParser {

        private final String currentRequest;

        CommandParser(@NotNull String request) {
            currentRequest = request;
        }

        /**
         * Parses input string into the sequence of command and arguments
         *
         * @return list of strings, where the first string is a command name, other strings are its arguments
         */
        public @NotNull ArrayList<String> getNext() {
            ArrayList<String> command = new ArrayList<>();
            boolean singleQuote = false;
            boolean doubleQuote = false;
            StringBuilder currentToken = new StringBuilder();
            for (char symbol : currentRequest.toCharArray()) {
                if (symbol == '\'' && !doubleQuote) {
                    singleQuote = !singleQuote;
                    if (currentToken.length() > 0) {
                        command.add(currentToken.toString());
                    }
                    currentToken = new StringBuilder();
                    continue;
                }
                if (symbol == '\"' && !singleQuote) {
                    doubleQuote = !doubleQuote;
                    if (currentToken.length() > 0) {
                        command.add(currentToken.toString());
                    }
                    currentToken = new StringBuilder();
                    continue;
                }
                if (singleQuote || doubleQuote) {
                    currentToken.append(symbol);
                    continue;
                }
                if (symbol == ' ') {
                    if (currentToken.length() > 0) {
                        command.add(currentToken.toString());
                    }
                    currentToken = new StringBuilder();
                    continue;
                }
                if (symbol == '=') {
                    if (currentToken.length() > 0) {
                        command.add(currentToken.toString());
                    }
                    currentToken = new StringBuilder();
                    command.add(0, "assignment");
                    continue;
                }
                currentToken.append(symbol);
            }
            if (currentToken.length() > 0) {
                command.add(currentToken.toString());
            }
            return command;
        }
    }
}
