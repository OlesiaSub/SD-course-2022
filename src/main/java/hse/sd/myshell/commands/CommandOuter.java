package hse.sd.myshell.commands;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CommandOuter implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandOuter.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    private static class StreamGobbler implements Runnable {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
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
        String homeDirectory = System.getProperty("user.dir");
        Process process;
        try {
            if (System.getProperty("os.name")
                    .toLowerCase().startsWith("windows")) {
                staticArgs.add(0, "cmd.exe");
            } else {
                staticArgs.add(0, "sh -c");
            }
            process = Runtime.getRuntime()
                    .exec(staticArgs.toArray(String[]::new));
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            if (process.waitFor() != 0) {
                exitCode = ExitCode.UNKNOWN_PROBLEM;
            }
        } catch (IOException | InterruptedException e) {
            exitCode = ExitCode.UNKNOWN_PROBLEM;
        }
        return new Result(new ArrayList<>(), exitCode);
    }
}
