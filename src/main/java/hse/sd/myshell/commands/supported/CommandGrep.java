package hse.sd.myshell.commands.supported;

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
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandGrep implements AbstractCommand {
    private ArrayList<String> staticArgs = new ArrayList<>();
    private final ArrayList<String> dynamicArgs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(CommandAssignment.class.getName());
    private ExitCode exitCode = ExitCode.OK;
    final CommandLineParser cliParser = new DefaultParser();

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

    public CommandGrep(@NotNull ArrayList<String> staticArgs, @NotNull ArrayList<String> dynamicArgs) {
        try {
            command = cliParser.parse(options, staticArgs.toArray(new String[0]), false);
            validateStaticArgs(new ArrayList<>(command.getArgList()));
        } catch (ParseException e) {
            exitCode = ExitCode.UNKNOWN_PROBLEM;
        }
    }

    @Override
    public void validateStaticArgs(ArrayList<String> args) {
        if (args.size() < 2) {
            exitCode = ExitCode.BAD_ARGS;
            return;
        } else {
            String file = args.get(1);
            Path path = Paths.get(file);
            if (!Files.exists(path)) {
                exitCode = ExitCode.BAD_ARGS;
                return;
            }
        }
        staticArgs = args;
    }

    @Override
    public void validateDynamicArgs(ArrayList<String> args) {
    }

    @Override
    public Result execute() {
        if (staticArgs.size() == 0){
            return new Result(new ArrayList<>(), exitCode);
        }
        String regularExpression = staticArgs.get(0);
        Pattern pattern;
        if (command.hasOption('w')) {
            regularExpression = "\\b" + regularExpression + "\\b";
        }
        if (command.hasOption('i')) {
            regularExpression = regularExpression.toLowerCase();
            pattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(regularExpression);
        }
        if (command.hasOption('A')) {
            if (Long.parseLong(command.getOptionValue('A')) < 0) {
                exitCode = ExitCode.BAD_ARGS;
                return new Result(new ArrayList<>(), exitCode);
            }
        }

        ArrayList<String> input;
        try (Stream<String> stream = Files.lines(Paths.get(staticArgs.get(1)))) {
            input = stream.collect(Collectors.toCollection(ArrayList::new));
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
}
