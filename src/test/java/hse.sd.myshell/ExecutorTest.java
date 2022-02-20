package hse.sd.myshell;

import hse.sd.myshell.commands.ExitCode;
import hse.sd.myshell.commands.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @TempDir
    File temporaryFolder;

    private final Executor executor = new Executor();

    public ExecutorTest() throws MyShellException {
    }

    @BeforeEach
    public void createTemporaryWorkingDir() {
        try {
            final File test_file1 = new File(temporaryFolder, "test_file.txt");
            FileWriter fw1 = new FileWriter(test_file1);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write("some content\n other content");
            bw1.close();
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
}
