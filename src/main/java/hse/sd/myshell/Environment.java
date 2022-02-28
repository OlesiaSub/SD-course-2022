package hse.sd.myshell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class, which contains information about assigned variables and current directory path
 */
public class Environment {
    private static final HashMap<String, String> variables = new HashMap<>();
    private static Path currentDirectoryPath = Paths.get("").toAbsolutePath().normalize();

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

    /**
     * @return current directory path
     */
    public static @NotNull Path getCurrentDirectoryPath() {
        return currentDirectoryPath;
    }

    /**
     * Sets [currentDirectoryPath] to absolute normalized version of [newCurrentDirectoryPath].
     *
     * @param newCurrentDirectoryPath new current directory path, can be absolute or relative
     */
    public static void setCurrentDirectoryPath(@NotNull Path newCurrentDirectoryPath) {
        Objects.requireNonNull(newCurrentDirectoryPath);
        currentDirectoryPath = newCurrentDirectoryPath.toAbsolutePath().normalize();
    }

    /**
     * @param path path to resolve in current directory, can be absolute or relative
     * @return normalized absolute path either resolved in current directory if it is not absolute or itself,
     * or null if path is invalid
     */
    public static @Nullable Path resolvePathInCurrentDirectory(@NotNull String path) {
        Objects.requireNonNull(path);
        try {
            return currentDirectoryPath.resolve(path).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            return null;
        }
    }

}
