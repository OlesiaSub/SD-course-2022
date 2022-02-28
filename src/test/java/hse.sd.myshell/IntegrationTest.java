package hse.sd.myshell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class IntegrationTest {

    private ByteArrayOutputStream testOut;
    private final Charset CHARSET = StandardCharsets.UTF_8;
    private String expectedOutput;

    public void redirectStdStreams(String input, String output) {
        expectedOutput = "Staring MyShell...\n" + output + ">> Exiting MyShell...\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes(CHARSET));
        testOut = new ByteArrayOutputStream();
        System.setIn(testIn);
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    public void checkStreams() {
        Assertions.assertEquals(expectedOutput, testOut.toString(CHARSET));
    }

    @Test
    public void testBasic() {
        redirectStdStreams("echo hello\nexit", ">> hello\n");
        MyShell.run();
    }

    @Test
    public void testChangeCurrentDirectoryCommands() throws IOException {
        String tempName = "tmp-myshell-folder-" + System.currentTimeMillis();
        String absoluteName = Path.of("").toAbsolutePath() + "/" + tempName;
        redirectStdStreams(
                "mkdir " + tempName + "\n" +
                        "cd " + tempName + "\n" +
                        "bash -c 'echo hello > file1'\n" +
                        "ls\n" +
                        "cat file1\n" +
                        "wc file1\n" +
                        "pwd\n" +
                        "touch file2\n" +
                        "ls\n",
                ">> \n>> >> \n>> file1\n>> hello\n\n>> 1 1 6 " + absoluteName + "/file1\n\n>> " + absoluteName + "\n>> \n>> file1  file2\n");
        MyShell.run();
        Files.delete(Path.of(tempName + "/file1"));
        Files.delete(Path.of(tempName + "/file2"));
        Files.delete(Path.of(tempName));
    }

}
