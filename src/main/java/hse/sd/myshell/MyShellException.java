package hse.sd.myshell;

public class MyShellException extends Exception {
    /**
     * Wrapper for MyShell exceptions
     * @param message will be handed over to exception super constructor
     */
    public MyShellException(String message) {
        super(message);
    }
}
