package hse.sd.myshell;

import hse.sd.myshell.commands.CommandExternal;
import hse.sd.myshell.commands.ExitCode;
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
    private CommandEcho echo;
    private CommandCat cat;
    private CommandWc wc;
    private CommandPwd pwd;
    private CommandAssignment assignment;
    private CommandExit exit;
    private CommandExternal outer;

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
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
    }

    @Test
    public void testEchoDynamicArgs() {
        echo = new CommandEcho(new ArrayList<>(), new ArrayList<>(List.of("test_value")));
        Result result = echo.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
    }

    @Test
    public void testEchoMultipleStaticArgs() {
        echo = new CommandEcho(new ArrayList<>(List.of("test_value1", "test_value2")), new ArrayList<>());
        Result result = echo.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value1 test_value2")), result.getResult());
    }

    @Test
    public void testEchoMultipleDynamicArgs() {
        echo = new CommandEcho(new ArrayList<>(), new ArrayList<>(List.of("test_value1", "test_value2")));
        Result result = echo.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("test_value1 test_value2")), result.getResult());
    }

    @Test
    public void testEchoDynamicAndStaticArgs() {
        echo = new CommandEcho(new ArrayList<>(List.of("test_value")), new ArrayList<>(List.of("test_value")));
        Result result = echo.execute();
        Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }

    @Test
    public void testEchoNoArgs() {
        echo = new CommandEcho(new ArrayList<>(), new ArrayList<>());
        Result result = echo.execute();
        Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }

    @Test
    public void testPwdStaticArgs() {
        pwd = new CommandPwd(new ArrayList<>(List.of("test_value")), new ArrayList<>());
        Result result = pwd.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testPwdDynamicArgs() {
        pwd = new CommandPwd(new ArrayList<>(), new ArrayList<>(List.of("test_value")));
        Result result = pwd.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testPwdDynamicAndStaticArgs() {
        pwd = new CommandPwd(new ArrayList<>(List.of("test_value")), new ArrayList<>(List.of("test_value")));
        Result result = pwd.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testPwdNoArgs() {
        pwd = new CommandPwd(new ArrayList<>(), new ArrayList<>());
        Result result = pwd.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
    }

    @Test
    public void testWcStaticArgs() {
        wc = new CommandWc(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt")), new ArrayList<>());
        Result result = wc.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(1, result.getResult().size());
        String[] res = result.getResult().get(0).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("3", res[1]);
        Assertions.assertEquals("20", res[2]);
    }

    @Test
    public void testWcDynamicArgs() {
        wc = new CommandWc(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt")));
        Result result = wc.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(1, result.getResult().size());
        String[] res = result.getResult().get(0).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("1", res[1]);
    }

    @Test
    public void testWcMultipleStaticArgs() {
        wc = new CommandWc(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")), new ArrayList<>());
        Result result = wc.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(2, result.getResult().size());
        String[] res = result.getResult().get(0).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("3", res[1]);
        Assertions.assertEquals("20", res[2]);
        res = result.getResult().get(1).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("5", res[1]);
        Assertions.assertEquals("31", res[2]);
    }

    @Test
    public void testWcMultipleDynamicArgs() {
        wc = new CommandWc(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")));
        Result result = wc.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(2, result.getResult().size());
        String[] res = result.getResult().get(0).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("1", res[1]);
        res = result.getResult().get(1).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("1", res[1]);
    }

    @Test
    public void testWcDynamicAndStaticArgs() {
        wc = new CommandWc(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")), new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")));
        Result result = wc.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(2, result.getResult().size());
        String[] res = result.getResult().get(0).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("3", res[1]);
        Assertions.assertEquals("20", res[2]);
        res = result.getResult().get(1).split(" ");
        Assertions.assertEquals("1", res[0]);
        Assertions.assertEquals("5", res[1]);
        Assertions.assertEquals("31", res[2]);
    }

    @Test
    public void testWcNoArgs() {
        wc = new CommandWc(new ArrayList<>(), new ArrayList<>());
        Result result = wc.execute();
        Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }

    @Test
    public void testCatStaticArgs() {
        cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt")), new ArrayList<>());
        Result result = cat.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("content of test file\n")), result.getResult());
    }

    @Test
    public void testCatDynamicArgs() {
        cat = new CommandCat(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt")));
        Result result = cat.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt\n")), result.getResult());
    }

    @Test
    public void testCatMultipleStaticArgs() {
        cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")), new ArrayList<>());
        Result result = cat.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("content of test file\nlong content of other test file\n")), result.getResult());
    }

    @Test
    public void testCatMultipleDynamicArgs() {
        cat = new CommandCat(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")));
        Result result = cat.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt\n" + temporaryFolder.getPath() + File.separator + "test_file2.txt\n")), result.getResult());
    }

    @Test
    public void testCatDynamicAndStaticArgs() {
        cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")), new ArrayList<>(List.of(temporaryFolder.getPath() + File.separator + "test_file1.txt", temporaryFolder.getPath() + File.separator + "test_file2.txt")));
        Result result = cat.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(List.of("content of test file\nlong content of other test file\n")), result.getResult());
    }

    @Test
    public void testCatNoArgs() {
        cat = new CommandCat(new ArrayList<>(), new ArrayList<>());
        Result result = cat.execute();
        Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
    }

    @Test
    public void testAssignmentStaticArgs() {
        assignment = new CommandAssignment(new ArrayList<>(List.of("a", "5")), new ArrayList<>());
        Result result = assignment.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertEquals(new ArrayList<>(), result.getResult());
        Assertions.assertEquals("5", Environment.getVariableValue("a"));
    }

    @Test
    public void testAssignmentBadStaticArgs() {
        assignment = new CommandAssignment(new ArrayList<>(List.of("a", "5", "6")), new ArrayList<>());
        Result result = assignment.execute();
        Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
    }

    @Test
    public void testAssignmentDynamicArgs() {
        assignment = new CommandAssignment(new ArrayList<>(List.of("a", "5")), new ArrayList<>(List.of("a", "5")));
        Result result = assignment.execute();
        Assertions.assertEquals(ExitCode.BAD_ARGS, result.getExitCode());
    }

    @Test
    public void testExit() {
        assignment = new CommandAssignment(new ArrayList<>(List.of("a", "5")), new ArrayList<>());
        assignment.execute();
        exit = new CommandExit(new ArrayList<>(), new ArrayList<>());
        Result result = exit.execute();
        Assertions.assertEquals(ExitCode.EXIT, result.getExitCode());
        Assertions.assertNull(Environment.getVariableValue("a"));
    }

    @Test
    public void testOuterCommandDir() {
        outer = new CommandExternal(new ArrayList<>(List.of("mkdir", temporaryFolder.getPath() + "/dir")), new ArrayList<>());
        Result result = outer.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertTrue(new File(temporaryFolder.getPath() + "/dir").exists());
        outer = new CommandExternal(new ArrayList<>(List.of("rmdir", temporaryFolder.getPath() + "/dir")), new ArrayList<>());
        result = outer.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertFalse(new File(temporaryFolder.getPath() + "/dir").exists());
    }

    @Test
    public void testOuterCommandTouch() {
        outer = new CommandExternal(new ArrayList<>(List.of("touch", temporaryFolder.getPath() + "/file.txt")), new ArrayList<>());
        Result result = outer.execute();
        Assertions.assertEquals(ExitCode.OK, result.getExitCode());
        Assertions.assertTrue(new File(temporaryFolder.getPath() + "/file.txt").exists());
    }

    @Test
    public void testNonExistingOuterCommand() {
        outer = new CommandExternal(new ArrayList<>(List.of("команда", temporaryFolder.getPath() + "/file.txt")), new ArrayList<>());
        Result result = outer.execute();
        Assertions.assertEquals(ExitCode.UNKNOWN_PROBLEM, result.getExitCode());
    }
}
