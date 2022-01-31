package hse.sd.myshell;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class Executor {

    public void executeAll(String commandSequence) throws MyShellException {
        CommandParser parser = new CommandParser(commandSequence);
        ArrayList<String> currentCommand;
        String currentOutput;
        while (!(currentCommand = parser.getNext()).equals(Collections.EMPTY_LIST)) {
            String commandName = currentCommand.get(0);
            currentCommand.remove(0);
            // todo разобраться с аргументами
            currentOutput = commandRedirect(commandName, currentCommand);
        }
    }

    private String commandRedirect(String commandName, ArrayList<String> effectiveParameters) throws MyShellException {
        String className = "Command" + commandName;
        String outerClassName = "CommandOuter";
        String instanceMethodName = "execute";
        Class<?>[] formalParameters = {ArrayList.class};
        String packageName = getClass().getPackage().getName() + "commands";
        String supportedPackageName = packageName + ".supported";
        Class<?> clazz = null;
        String output = "";
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
             output = (String) method.invoke(newInstance, effectiveParameters); // todo: is casting ok?
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
            ArrayList<String> nextCommand = new ArrayList<>();
            boolean singleQuote = false;
            boolean doubleQuote = false;
            String currentToken = "";
            for (char symbol : currentRequest.toCharArray()) {
                if (symbol == '\'') singleQuote = true;
                if (symbol == '"') doubleQuote = true;
            }
            return nextCommand;
        }
    }
}
