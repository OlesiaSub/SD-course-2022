package hse.sd.myshell.commands.supported;

import hse.sd.myshell.LoggerWithHandler;
import hse.sd.myshell.MyShellException;
import hse.sd.myshell.commands.AbstractCommand;
import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class corresponding to the command grep.
 */
public class CommandGrep implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private ArrayList<String> dynamicArgs = new ArrayList<>();
    private Logger logger = Logger.getLogger(CommandAssignment.class.getName());
    private ExitCode exitCode = ExitCode.OK;

    private static final Options options = new Options();
    private CommandLine command;

    static {
        options.addOption(new Option("w", "--word-regexp", false,
                "Select  only  those  lines  containing  matches  that form whole words."));
        options.addOption(new Option("A", "--after-context", true,
                "Print NUM  lines  of  trailing  context  after  matching  lines."));
        options.addOption(new Option("i", "--ignore-case", false,
                "Ignore  case  distinctions  in  patterns and input data, so that " +
                        "characters that differ only in case match each other."));
    }

    public CommandGrep(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) throws MyShellException {
        logger = (new LoggerWithHandler(CommandEcho.class.getName())).getLogger();
        try {
            validateDynamicArgs(dynamicArgs);
            CommandLineParser cliParser = new DefaultParser();
            command = cliParser.parse(options, staticArgs.toArray(new String[0]), false);
            validateStaticArgs(new ArrayList<>(command.getArgList()));
        } catch (ParseException e) {
            exitCode = ExitCode.UNKNOWN_PROBLEM;
        }
    }

    /**
     * Check args, save if correct, update exitCode if incorrect.
     * First static arg should be pattern.
     * If the second static arg exists, it should be existing file,
     * Otherwise one dynamic arg should exist.
     *
     * @param args a sequence of args for validating.
     */
    @Override
    public void validateStaticArgs(@NotNull ArrayList<String> args) {
        if (args.size() < 1 || (args.size() < 2 && dynamicArgs.isEmpty())) {
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Not enough args");
            return;
        } else if (args.size() > 1) {
            String file = args.get(1);
            Path path = Paths.get(file);
            if (!Files.exists(path)) {
                logger.log(Level.WARNING, "File does not exist: " + file);
                exitCode = ExitCode.BAD_ARGS;
                return;
            }
        }
        staticArgs = args;
    }

    @Override
    public void validateDynamicArgs(@NotNull ArrayList<String> args) {
        if (args.size() > 1) {
            logger.log(Level.WARNING, "More than 1 dynamic arg");
            exitCode = ExitCode.BAD_ARGS;
            return;
        }
        dynamicArgs = args;
    }

    /**
     * Search for PATTERN in FILE.
     * Concatenate lines that match a pattern into one line.
     *
     * @return exitCode and result list.
     */
    @Override
    public Result execute() {
        if (staticArgs.size() == 0) {
            return new Result(new ArrayList<>(), exitCode);
        }

        Pattern pattern;
        try {
            pattern = ApplyOptions();
        } catch (MyShellException e){
            exitCode = ExitCode.BAD_ARGS;
            logger.log(Level.WARNING, "Bad NUM");
            return new Result(new ArrayList<>(), exitCode);
        }

        ArrayList<String> input;
        try {
            input = getInput();
        } catch (IOException e) {
            exitCode = ExitCode.UNKNOWN_PROBLEM;
            return new Result(new ArrayList<>(), exitCode);
        }

        StringBuilder result = new StringBuilder();
        long num = 0;
        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                result.append(line);
                result.append('\n');
                num = Long.parseLong(command.getOptionValue('A', "0"));
                continue;
            }
            if (num > 0) {
                result.append(line);
                result.append('\n');
                num--;
            }
        }
        return new Result(new ArrayList<>(Collections.singleton(result.toString())), exitCode);
    }

    /**
     * Get input:
     * Strings from file if static args are existed or dynamic argument.
     */
    private ArrayList<String> getInput() throws IOException {
        ArrayList<String> input;
        if (staticArgs.size() > 1) {
            Stream<String> stream = Files.lines(Paths.get(staticArgs.get(1)));
            input = stream.collect(Collectors.toCollection(ArrayList::new));
        } else {
            input = new ArrayList<>(Collections.singleton(dynamicArgs.get(0)));
        }
        return input;
    }

    /**
     * Apply options and generate pattern
     */
    private @NotNull Pattern ApplyOptions() throws MyShellException {
        String regularExpression = staticArgs.get(0);
        Pattern pattern;
        if (command.hasOption('w')) {
            regularExpression = "\\b" + regularExpression + "\\b";
        }
        if (command.hasOption('i')) {
            pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(regularExpression);
        }
        if (command.hasOption('A')) {
            if (Long.parseLong(command.getOptionValue('A')) < 0) {
                throw new MyShellException("invalid args");
            }
        }
        return pattern;
    }
}
