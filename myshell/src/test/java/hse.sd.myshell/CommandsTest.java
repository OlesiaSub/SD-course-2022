package hse.sd.myshell;

import hse.sd.myshell.commands.Result;
import hse.sd.myshell.commands.supported.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandsTest {
    public CommandEcho echo;
    public CommandCat cat;
    public CommandWc wc;
    public CommandPwd pwd;
    public CommandAssignment assignment;

    @TempDir
    File temporaryFolder;

    @BeforeEach
    public void createTempFiles() {
        try {
            final File test_file1 = new File(temporaryFolder, "test_file1.txt");
            final File test_file2 = new File(temporaryFolder, "test_file2.txt");
            FileWriter fw1 = new FileWriter(test_file1);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write("content of test file");
            bw1.close();
            FileWriter fw2 = new FileWriter(test_file2);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write("long content of other test file");
            bw2.close();
        } catch (IOException e) {
            System.out.println("Problems with test files");
        }
    }

    @Test
    public void testEchoStaticArgs() {
        echo = new CommandEcho(new ArrayList<>(List.of("test_value")), new ArrayList<>());
        Result result = echo.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
    }

    @Test
    public void testEchodynamicArgs() {
        echo = new CommandEcho(new ArrayList<>(), new ArrayList<>(List.of("test_value")));
        Result result = echo.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
    }

    @Test
    public void testEchoMultipleStaticArgs() {
        echo = new CommandEcho(new ArrayList<>(List.of("test_value1", "test_value2")), new ArrayList<>());
        Result result = echo.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value1 test_value2")), result.getResult());
    }

    @Test
    public void testEchoMultipledynamicArgs() {
        echo = new CommandEcho(new ArrayList<>(), new ArrayList<>(List.of("test_value1", "test_value2")));
        Result result = echo.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value1 test_value2")), result.getResult());
    }

    @Test
    public void testEchodynamicAndStaticArgs() {
        echo = new CommandEcho(new ArrayList<>(List.of("test_value")), new ArrayList<>(List.of("test_value")));
        Result result = echo.execute();
        Assertions.assertEquals(1, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }

    @Test
    public void testEchoNoArgs() {
        echo = new CommandEcho(new ArrayList<>(), new ArrayList<>());
        Result result = echo.execute();
        Assertions.assertEquals(1, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }

    @Test
    public void testPwdStaticArgs() {
        pwd = new CommandPwd(new ArrayList<>(List.of("test_value")), new ArrayList<>());
        Result result = pwd.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testPwddynamicArgs() {
        pwd = new CommandPwd(new ArrayList<>(), new ArrayList<>(List.of("test_value")));
        Result result = pwd.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testPwddynamicAndStaticArgs() {
        pwd = new CommandPwd(new ArrayList<>(List.of("test_value")), new ArrayList<>(List.of("test_value")));
        Result result = pwd.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testPwdNoArgs() {
        pwd = new CommandPwd(new ArrayList<>(), new ArrayList<>());
        Result result = pwd.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testCatStaticArgs() {
        cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt")), new ArrayList<>());
        Result result = cat.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("content of test file\n")), result.getResult());
    }

    @Test
    public void testCatdynamicArgs() {
        cat = new CommandCat(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt")));
        Result result = cat.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt\n")), result.getResult());
    }

    @Test
    public void testCatMultipleStaticArgs() {
        cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt", temporaryFolder.getPath() + '\\' + "test_file2.txt")), new ArrayList<>());
        Result result = cat.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("content of test file\nlong content of other test file\n")), result.getResult());
    }

    @Test
    public void testCatMultipledynamicArgs() {
        cat = new CommandCat(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt", temporaryFolder.getPath() + '\\' + "test_file2.txt")));
        Result result = cat.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt\n" + temporaryFolder.getPath() + '\\' + "test_file2.txt\n")), result.getResult());
    }

    @Test
    public void testCatdynamicAndStaticArgs() {
        cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt", temporaryFolder.getPath() + '\\' + "test_file2.txt")), new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' + "test_file1.txt", temporaryFolder.getPath() + '\\' + "test_file2.txt")));
        Result result = cat.execute();
        Assertions.assertEquals(0, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("content of test file\nlong content of other test file\n")), result.getResult());
    }

    @Test
    public void testCatNoArgs() {
        cat = new CommandCat(new ArrayList<>(), new ArrayList<>());
        Result result = cat.execute();
        Assertions.assertEquals(1, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }
}
