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
        public void testEchoStaticArgs () {
            echo = new CommandEcho(new ArrayList<>(List.of("test_value")), null);
            Result result = echo.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
        }

        @Test
        public void testEchoDinamicArgs () {
            echo = new CommandEcho(null, new ArrayList<>(List.of("test_value")));
            Result result = echo.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
        }

        @Test
        public void testEchoMultipleStaticArgs () {
            echo = new CommandEcho(new ArrayList<>(List.of("test_value1", "test_value2")), null);
            Result result = echo.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("test_value1 test_value2")), result.getResult());
        }

        @Test
        public void testEchoMultipleDinamicArgs () {
            echo = new CommandEcho(null, new ArrayList<>(List.of("test_value1", "test_value2")));
            Result result = echo.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("test_value1 test_value2")), result.getResult());
        }

        @Test
        public void testEchoDinamicAndStaticArgs () {
            echo = new CommandEcho(new ArrayList<>(List.of("test_value")), new ArrayList<>(List.of("test_value")));
            Result result = echo.execute();
            Assertions.assertEquals(1, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(), result.getResult());
        }

        @Test
        public void testEchoNoArgs () {
            echo = new CommandEcho(null, null);
            Result result = echo.execute();
            Assertions.assertEquals(1, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(), result.getResult());
        }

        @Test
        public void testEchoEmptyArgs () {
            echo = new CommandEcho(new ArrayList<>(), new ArrayList<>());
            Result result = echo.execute();
            Assertions.assertEquals(1, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(), result.getResult());
        }

        @Test
        public void testEchoEmptyStaticArgs () {
            echo = new CommandEcho(new ArrayList<>(), new ArrayList<>(List.of("test_value")));
            Result result = echo.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
        }

        @Test
        public void testEchoEmptyDinamicArgs () {
            echo = new CommandEcho(new ArrayList<>(List.of("test_value")), new ArrayList<>());
            Result result = echo.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("test_value")), result.getResult());
        }

        @Test
        public void testPwdStaticArgs () {
            pwd = new CommandPwd(new ArrayList<>(List.of("test_value")), null);
            Result result = pwd.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
        }

        @Test
        public void testPwdDinamicArgs () {
            pwd = new CommandPwd(null, new ArrayList<>(List.of("test_value")));
            Result result = pwd.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
        }

        @Test
        public void testPwdDinamicAndStaticArgs () {
            pwd = new CommandPwd(new ArrayList<>(List.of("test_value")), new ArrayList<>(List.of("test_value")));
            Result result = pwd.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
        }

        @Test
        public void testPwdNoArgs () {
            pwd = new CommandPwd(null, null);
            Result result = pwd.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
        }

        @Test
        public void testPwdEmptyArgs () {
            pwd = new CommandPwd(new ArrayList<>(), new ArrayList<>());
            Result result = pwd.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(System.getProperty("user.dir"))), result.getResult());
        }

        @Test
        public void testCatStaticArgs () {
            cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt")), null);
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("content of test file\n")), result.getResult());
        }

        @Test
        public void testCatDinamicArgs () {
            cat = new CommandCat(null, new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt")));
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt\n")), result.getResult());
        }

        @Test
        public void testCatMultipleStaticArgs () {
            cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt", temporaryFolder.getPath() + '\\' +"test_file2.txt")), null);
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("content of test file\nlong content of other test file\n")), result.getResult());
        }

        @Test
        public void testCatMultipleDinamicArgs () {
            cat = new CommandCat(null, new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt", temporaryFolder.getPath() + '\\' +"test_file2.txt")));
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt\n" + temporaryFolder.getPath() + '\\' +"test_file2.txt\n")), result.getResult());
        }

        @Test
        public void testCatDinamicAndStaticArgs () {
            cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt", temporaryFolder.getPath() + '\\' +"test_file2.txt")), new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt", temporaryFolder.getPath() + '\\' +"test_file2.txt")));
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("content of test file\nlong content of other test file\n")), result.getResult());
        }

        @Test
        public void testCatNoArgs () {
            cat = new CommandCat(null, null);
            Result result = cat.execute();
            Assertions.assertEquals(1, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(), result.getResult());
        }

        @Test
        public void testCatEmptyArgs () {
            cat = new CommandCat(new ArrayList<>(), new ArrayList<>());
            Result result = cat.execute();
            Assertions.assertEquals(1, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(), result.getResult());
        }

        @Test
        public void testCatEmptyStaticArgs () {
            cat = new CommandCat(new ArrayList<>(), new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt")));
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt\n")), result.getResult());
        }

        @Test
        public void testCatEmptyDinamicArgs () {
            cat = new CommandCat(new ArrayList<>(List.of(temporaryFolder.getPath() + '\\' +"test_file1.txt")), new ArrayList<>());
            Result result = cat.execute();
            Assertions.assertEquals(0, result.getExitcode());
            Assertions.assertEquals(new ArrayList<>(List.of("content of test file\n")), result.getResult());
        }
    }
