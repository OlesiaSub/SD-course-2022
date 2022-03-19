package hse.sd.myshell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
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
        Assertions.assertEquals(expectedOutput, testOut.toString(CHARSET).replace("\r\n", "\n"));
    }

    @Test
    public void testBasic() {
        redirectStdStreams("echo hello\nexit", ">> hello\n");
        MyShell.run();
    }

    @Test
    public void testChangeCurrentDirectoryCommands() throws IOException {
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        String shell = isWindows ? "cmd /c" : "bash -c";
        String tempName = "tmp-myshell-folder-" + System.currentTimeMillis();
        String absoluteName = Path.of("").toAbsolutePath() + File.separator + tempName;
        redirectStdStreams(
                shell + " 'mkdir " + tempName + "'\n" +
                        "cd " + tempName + "\n" +
                        shell + " 'echo hello > file1'\n" +
                        "ls\n" +
                        "cat file1\n" +
                        "wc file1\n" +
                        "pwd\n" +
                        (isWindows ? shell + " 'echo.> file2'\n" : "touch file2\n") +
                        "ls\n",
                (isWindows ?
                        ">> \n>> >> \n>> file1\n>> hello \n\n>> 1 1 8 "
                        :
                        ">> \n>> >> \n>> file1\n>> hello\n\n>> 1 1 6 ")
                        + absoluteName + File.separator + "file1\n\n>> " + absoluteName + "\n>> \n>> file1  file2\n");
        MyShell.run();
        Files.delete(Path.of(tempName + "/file1"));
        Files.delete(Path.of(tempName + "/file2"));
        Files.delete(Path.of(tempName));
    }

}
