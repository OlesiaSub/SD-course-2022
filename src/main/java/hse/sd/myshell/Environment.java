package hse.sd.myshell;

import java.util.HashMap;

/**
 * Class, which contains information about assigned variables
 */
public class Environment {
    private static final HashMap<String, String> variables = new HashMap<>();

    /**
     * @param variable requested variable name
     * @return value, which was last assigned to the requested variable
     */
    public static String getVariableValue(String variable) {
        return variables.getOrDefault(variable, null);
    }

    /**
     * @param variable name of the variable, to which user wants to assign value
     * @param value    value to be assigned
     */
    public static void setVariableValue(String variable, String value) {
        variables.put(variable, value);
    }

    /**
     * Clears variables storage
     */
    public static void cleanVariables() {
        variables.clear();
    }

    /**
     * @return hashmap, which contains information about all variables
     */
    public static HashMap<String, String> getEnvironment() {
        return variables;
    }
}
