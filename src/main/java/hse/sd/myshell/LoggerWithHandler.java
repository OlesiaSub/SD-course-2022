package hse.sd.myshell;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class which helps to write logs into one file
 */
public class LoggerWithHandler {
    String className;
    FileHandler fileHandler;

    public LoggerWithHandler(String className) throws MyShellException {
        this.className = className;
        fileHandler = FileHandlerSingleton.getInstance();
    }

    /**
     * @return Logger which writes logs only in file
     */
    public Logger getLogger() {
        Logger logger = Logger.getLogger(className);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        return logger;
    }

    /**
     * Class which has only one FileHandler instance (using Singleton pattern) and helps to write logs in one particular file
     */
    private static class FileHandlerSingleton {
        private static FileHandler instance;

        private FileHandlerSingleton() {
        }

        /**
         * @return instance of FileHandler (creates it if it does not exist or gives an existing one)
         * @throws MyShellException
         */
        public static FileHandler getInstance() throws MyShellException {
            if (instance == null) {
                try {
                    instance = new FileHandler("loggingResult.log");
                } catch (IOException e) {
                    throw new MyShellException("Something went wrong with file where to print logs.");
                }
            }
            return instance;
        }
    }
}
