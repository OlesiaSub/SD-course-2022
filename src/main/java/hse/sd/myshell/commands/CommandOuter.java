package hse.sd.myshell.commands;

import hse.sd.myshell.Environment;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandOuter implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandOuter.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    private static class MyConsumer implements Consumer<String> {
        private final ArrayList<String> result = new ArrayList<>();

        @Override
        public void accept(String s) {
            result.add(s);
        }
    }

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }

    public CommandOuter(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        validateStaticArgs(staticArgs);
        validateDynamicArgs(dynamicArgs);
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        staticArgs = args;
    }

    @Override
    public void validateDynamicArgs(ArrayList<String> args) {
        dynamicArgs = args;
    }

    @Override
    public Result execute() {
        MyConsumer consumer = new MyConsumer();
        try {
            ProcessBuilder builder = new ProcessBuilder(staticArgs);
            builder.directory(new File(System.getProperty("user.dir")));
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            builder.environment().putAll(Environment.getEnvironment());
            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), consumer);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            if (process.waitFor() != 0) {
                logger.log(Level.WARNING, "Exit code " + process.exitValue());
                exitCode = ExitCode.UNKNOWN_PROBLEM;
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.WARNING, e.getMessage());
            exitCode = ExitCode.UNKNOWN_PROBLEM;
        }
        return new Result(consumer.result, exitCode);
    }
}
