package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import hse.sd.myshell.commands.supported.CommandCat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExecutorTest {

    private final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

    @TempDir
    File temporaryFolder;

    private final Executor executor = new Executor();

    public ExecutorTest() throws MyShellException {
    }

    @BeforeEach
    public void createTemporaryWorkingDir() {
        try {
            final File test_file = new File(temporaryFolder, "test_file.txt");
            FileWriter fw1 = new FileWriter(test_file);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write("some content\n other content");
            bw1.close();
            final File script = new File(temporaryFolder, "script.sh");
            FileWriter fw2 = new FileWriter(script);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write("#!/bin/bash\n" +
                    "echo \"Hello, \" $1");
            bw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSingleCommandExecution() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("echo hello");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("hello")), result.getResult());
        });
    }

    @Test
    public void testSupportedPipesExecution() {
        // todo : implement after wc files
    }

    @Test
    public void testPipesWithExternalExecution() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("ls | pwd");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
        });
    }

    @Test
    public void testPipesWithExitExecution() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("ls | exit | pwd");
            Assertions.assertEquals(ExitCode.EXIT, result.getExitCode());
            Assertions.assertEquals(Collections.EMPTY_LIST, result.getResult());
        });
    }

    @Test
    public void testPipeAtTheEnd() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("ls |");
            Assertions.assertEquals(ExitCode.UNKNOWN_PROBLEM, result.getExitCode());
        });
    }

    @Test
    public void testJustPipe() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("|");
            Assertions.assertEquals(ExitCode.UNKNOWN_PROBLEM, result.getExitCode());
        });
    }

    @Test
    public void testSeveralPipesBetweenCommands() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("ls | | pwd");
            Assertions.assertEquals(ExitCode.UNKNOWN_PROBLEM, result.getExitCode());
        });
    }

    @Test
    public void testCommandNameInQuotes() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("\"echo\" hello");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("hello")), result.getResult());
        });
    }

    @Test
    public void testEchoQuote() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("echo \"'\"");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("'")), result.getResult());
        });
    }

    @Test
    public void testArgumentWithQuotes() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("cat "
                    + temporaryFolder.getPath() + File.separator + "test_'file.txt'");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("some content\n other content")), result.getResult());
        });
    }

    @Test
    public void testArgumentWithQuotesFirst() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("cat "
                    + temporaryFolder.getPath() + File.separator + "'test_'file.txt");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("some content\n other content")), result.getResult());
        });
    }

    @Test
    public void testExternalCommandStreams() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("echo file | cat");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("file")), result.getResult());
        });
    }

    @Test
    public void testExternalCommandArgumentStream() { // TODO: failing on Windows
        if(isWindows) return;
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("echo olesya | bash "
                    + temporaryFolder.getPath() + File.separator + "script.sh");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("Hello, \n")), result.getResult());
        });
    }

    @Test
    public void testSimpleAssignmentSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("x=hello");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        });
    }

    @Test
    public void testAssignmentQuotesSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("x=\"hello\"");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        });
    }

    @Test
    public void testAssignmentQuotesSpacesSuccess() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("x=\"   hello \"");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        });
    }

    @Test
    public void testAssignmentVariableInQuotes() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("\"x\"=hell");
            Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
        });
    }

    @Test
    public void testNotEnclosedQuotes() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("x=\"hello");
            Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
            result = executor.executeAll("echo hello | echo \"");
            Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
        });
    }

    @Test
    public void testArgumentInQuotesExternal() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("grep \"some\" "
                    + temporaryFolder.getPath() + File.separator + "'test_'file.txt");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
            Assertions.assertEquals(new ArrayList<>(List.of("some content\n")), result.getResult());
        });
    }

    @Test
    public void testAssignmentToVariableContainingEQ() {
        Assertions.assertDoesNotThrow(() -> {
            Result result = executor.executeAll("x='y=z'");
            Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        });
    }
}
