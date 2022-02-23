package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for commands execution
 */
public class Executor {

    private final Logger logger;

    public Executor() throws MyShellException {
        logger = (new LoggerWithHandler(Executor.class.getName())).getLogger();
    }

    /**
     * Parses user input, transfers it to the corresponding class and executes it there
     *
     * @param commandSequence a sequence of commands entered by user
     * @return Result, which contains result and exit code of the last executed command
     * @throws MyShellException in case of any mistakes that were not handled by the application
     */
    public Result executeAll(@NotNull String commandSequence) throws MyShellException {
        CommandParser parser = new CommandParser(commandSequence);
        ArrayList<String> staticArgs = new ArrayList<>();
        ArrayList<String> dynamicArgs = new ArrayList<>();
        Result result = new Result(new ArrayList<>(), ExitCode.EXIT);
        ArrayList<String> prevResult = new ArrayList<>();
        while (true) {
            try {
                staticArgs = parser.getNext();
            } catch (MyShellException e) {
                System.out.println(e.getMessage());
                return new Result(staticArgs, ExitCode.BAD_ARGS);
            }
            if (staticArgs.equals(Collections.EMPTY_LIST)) {
                break;
            }
            dynamicArgs.clear();
            String commandName = staticArgs.remove(0);
            if (!prevResult.isEmpty()) {
                dynamicArgs.addAll(prevResult);
            }
            result = launchCorrespondingCommand(commandName, staticArgs, dynamicArgs);
            prevResult = result.getResult();
            switch (result.getExitCode()) {
                case EXIT: {
                    logger.log(Level.INFO, "Execution finished with exit code "
                            + result.getExitCode() + ", exiting the application");
                    return result;
                }
                case BAD_ARGS: {
                    logger.log(Level.WARNING, "Execution finished with exit code " + result.getExitCode());
                    System.out.println("Wrong arguments were provided for command " + commandName +
                            ". Static arguments: " + staticArgs + ", dynamic arguments: " + dynamicArgs);
                    return result;
                }
                case OK: {
                    logger.log(Level.WARNING, "Execution finished with exit code " + result.getExitCode());
                    break;
                }
                case UNKNOWN_PROBLEM: {
                    logger.log(Level.INFO, "Execution finished with exit code " + result.getExitCode());
                    System.out.println("An unknown problem occurred during execution.");
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * Loads class of the command called [commandName], creates its instance and invokes its [execute] method
     * If the command [commandName] is not supported - it redirects it to the CommandExternal class to be processed there
     *
     * @param commandName name of the command to be processed
     * @param staticArgs  its static arguments
     * @param dynamicArgs its dynamic arguments
     * @return result and exit code of the execution
     * @throws MyShellException in case there were errors during the execution
     */
    private Result launchCorrespondingCommand(@NotNull String commandName, @NotNull ArrayList<String> staticArgs,
                                              @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        String supportedCommandName = commandName;
        if (commandName.equals(commandName.toLowerCase())) {
            supportedCommandName = commandName.substring(0, 1).toUpperCase(Locale.ROOT)
                    + commandName.substring(1).toLowerCase(Locale.ROOT);
        }
        String instanceMethodName = "execute";
        Class<?>[] formalParameters = {ArrayList.class, ArrayList.class};
        Class<?> launchingClass = getLaunchingClass(supportedCommandName, commandName, staticArgs);
        return processInvocation(launchingClass, formalParameters, staticArgs, dynamicArgs, instanceMethodName, commandName);
    }

    /**
     * Checks if command [commandName] is supported.
     * If there is no class that supports current command - it will be redirected to CommandExternal.
     *
     * @return class which will process command invocation
     */
    private Class<?> getLaunchingClass(String supportedCommandName, String commandName, ArrayList<String> staticArgs)
            throws MyShellException {
        String packageName = getClass().getPackage().getName() + ".commands";
        String supportedPackageName = packageName + ".supported";
        String className = "Command" + supportedCommandName;
        String externalClassName = "CommandExternal";
        Class<?> launchingClass = null;
        try {
            launchingClass = Class.forName(supportedPackageName + "." + className);
        } catch (ClassNotFoundException e) {
            logger.log(Level.INFO, "Required command \"" + commandName +
                    "\" is not supported and will be identified as an external command");
        }
        if (launchingClass == null) {
            try {
                launchingClass = Class.forName(packageName + "." + externalClassName);
                staticArgs.add(0, commandName);
            } catch (ClassNotFoundException e) {
                throw new MyShellException("Unable to execute external command " + commandName);
            }
        }
        return launchingClass;
    }

    /**
     * Creates an instance of class [launchingClass], invokes command [commandName] and returns invocation Result
     */
    private Result processInvocation(Class<?> launchingClass, Class<?>[] formalParameters, ArrayList<String> staticArgs,
                                     ArrayList<String> dynamicArgs, String instanceMethodName, String commandName)
            throws MyShellException {
        Result output;
        try {
            Object newInstance = launchingClass.getDeclaredConstructor(formalParameters).newInstance(staticArgs, dynamicArgs);
            Method method = launchingClass.getMethod(instanceMethodName);
            output = (Result) method.invoke(newInstance);
        } catch (NoSuchMethodException e) {
            throw new MyShellException("Can not find required method of the command " + commandName);
        } catch (InstantiationException e) {
            throw new MyShellException("Can not instantiate command " + commandName + "'s class");
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println(e.getMessage() + '\n' + e.getCause());
            throw new MyShellException("Can not invoke required method of the command " + commandName);
        }
        return output;
    }

    /**
     * Class for parsing user input into sequence of commands
     */
    private static class CommandParser {

        private String currentRequest;

        CommandParser(@NotNull String request) {
            currentRequest = request;
        }

        /**
         * Parses input string into the sequence of command and arguments
         * Examples of parsing: 1. echo x=y --> ["echo", "x=y"] 2. echo=x --> ["assignment", "echo", "x=y"]
         * If the first string before whitespace does not contain '=' in it - it will be identified as command name.
         * All the remaining strings will be identified as its arguments.
         * Otherwise, everything after '=' will be assigned to the variable located before '='.
         *
         * @return list of strings, where the first string is a command name, other strings are its arguments
         */
        public @NotNull ArrayList<String> getNext() throws MyShellException {
            if (currentRequest.isEmpty()) {
                return new ArrayList<>();
            }
            ArrayList<String> command = new ArrayList<>();
            boolean singleQuote = false;
            boolean doubleQuote = false;
            StringBuilder currentToken = new StringBuilder();
            int cut = 0;
            char prev = ' ';
            boolean wasAssignment = false;
            boolean wasCommand = false;
            boolean first = true;
            boolean wasSymbol = false;
            boolean assignmentQuotes = false;
            for (char symbol : currentRequest.toCharArray()) {
                cut++;
                if (!wasSymbol && symbol != '|' && symbol != ' ') {
                    wasSymbol = true;
                }
                if (symbol == '\'' && !doubleQuote) {
                    boolean setAssignment = false;
                    if (!singleQuote) {
                        if (prev == '=') {
                            setAssignment = true;
                            if (currentToken.length() > 0) {
                                command.add(currentToken.toString());
                            }
                            currentToken.append(symbol);
//                            currentToken = new StringBuilder();
                        }
                    }
                    if (assignmentQuotes) {
                        assignmentQuotes = false;
                        currentToken.append(symbol);
                    }
                    if (setAssignment) {
                        assignmentQuotes = true;
                    }
                    singleQuote = !singleQuote;
                    prev = symbol;
                    continue;
                }
                if (symbol == '\"' && !singleQuote) {
                    boolean setAssignment = false;
                    if (!doubleQuote) {
                        if (prev == '=') {
                            setAssignment = true;
                            if (currentToken.length() > 0) {
                                command.add(currentToken.toString());
                            }
                            currentToken.append(symbol);
//                            currentToken = new StringBuilder();
                        }
                    }
                    if (assignmentQuotes) {
                        assignmentQuotes = false;
                        currentToken.append(symbol);
                    }
                    if (setAssignment) {
                        assignmentQuotes = true;
                    }
                    doubleQuote = !doubleQuote;
                    prev = symbol;
                    continue;
                }

                if (singleQuote || doubleQuote) {
                    currentToken.append(symbol);
                    prev = symbol;
                    continue;
                }
                if (symbol == '|') {
                    if (!wasSymbol) {
                        command.add("|");
                    }
                    break;
                }
                if (symbol == ' ') {
                    if (assignmentQuotes && (prev == '"' || prev == '\'')) {
                        assignmentQuotes = false;
                        currentToken.append(symbol);
                    }
                    if (currentToken.length() > 0) {
                        command.add(currentToken.toString());
                    }
                    if (!wasAssignment && first) {
                        first = false;
                        wasCommand = true;
                    }
                    currentToken = new StringBuilder();
                    prev = symbol;
                    continue;
                }
                if (symbol == '=' && prev != ' ' && !wasAssignment && !wasCommand) {
                    wasAssignment = true;
                    if (prev == '"' || prev == '\'') {
                        currentToken.append('"');
                    }
                    if (currentToken.length() > 0) {
                        command.add(currentToken.toString());
                    }
                    currentToken = new StringBuilder();
                    command.add(0, "assignment");
                    prev = symbol;
                    continue;
                }
                currentToken.append(symbol);
                prev = symbol;
            }
            if (singleQuote || doubleQuote) {
                throw new MyShellException("Quotes were not closed.");
            }
            if (currentToken.length() > 0) {
                command.add(currentToken.toString());
            }
            if (currentRequest.substring(cut - 1).matches("^[ |]+$")) {
                currentRequest = currentRequest.substring(cut - 1);
            } else {
                currentRequest = currentRequest.substring(cut);
            }
            return command;
        }
    }
}
