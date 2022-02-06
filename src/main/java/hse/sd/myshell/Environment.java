package hse.sd.myshell;

import java.util.HashMap;

public class Environment {
    private static final HashMap<String, String> variables = new HashMap<>();

    public static String getVariableValue(String variable) {
        return variables.getOrDefault(variable, null);
    }

    public static void setVariableValue(String variable, String value) {
        variables.put(variable, value);
    }

    public static void cleanVariables() {
        variables.clear();
    }

    public static HashMap<String, String> getEnvironment() {
        return variables;
    }
}
