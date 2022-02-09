package hse.sd.myshell;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PreprocessorTest {

    @Test
    public void testPreprocessEmptyString() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "";
        String result = preprocessor.process(str);
        Assertions.assertEquals("", result);
    }

    @Test
    public void testPreprocessNoSubstitutions() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo Hello world";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessNoSubstitutionsWithQuotes() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo \'Hello world\'";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessNoSubstitutionsWithDoubleQuotes() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo \"Hello world\"";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessNoSubstitutionsWithOneQuote() { //important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo \"Hello world";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessWithSubstitutionNoQuotes() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "cat $FILE";
        Environment.setVariableValue("FILE", "example.txt");
        String result = preprocessor.process(str);
        Assertions.assertEquals("cat example.txt", result);
    }

    @Test
    public void testPreprocessWithSubstitutionQuotes() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "cat '$FILE'";
        Environment.setVariableValue("FILE", "example.txt");
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessWithSubstitutionDoubleQuotes() { //important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "cat \"$FILE\"";
        Environment.setVariableValue("FILE", "example.txt");
        String result = preprocessor.process(str);
        Assertions.assertEquals("cat \"example.txt\"", result);
    }

    @Test
    public void testPreprocessNoSubstitutionsWithPipe1() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "cat example.txt | wc";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessNoSubstitutionsWithPipe2() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo 123 | wc";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessHardSubstitutions() {
        Preprocessor preprocessor = new Preprocessor();
        String str = "$x$y";
        Environment.setVariableValue("x", "ex");
        Environment.setVariableValue("y", "it");
        String result = preprocessor.process(str);
        Assertions.assertEquals("exit", result);
    }

    @Test
    public void testPreprocessSubstitutionsOneNumber() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $1";
        String result = preprocessor.process(str);
        Assertions.assertEquals("echo $1 ", result);
    }

    @Test
    public void testPreprocessSubstitutionsOneNumberAndLetters() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $1ab";
        String result = preprocessor.process(str);
        Assertions.assertEquals("echo $1 ab", result);
    }

    @Test
    public void testPreprocessStrangeSubstitutions1() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessStrangeSubstitutions2() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $$";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessStrangeSubstitutions3() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $!";
        String result = preprocessor.process(str);
        Assertions.assertEquals(str, result);
    }

    @Test
    public void testPreprocessStrangeSubstitutions4() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $x";
        Environment.setVariableValue("x", "$$$$");
        String result = preprocessor.process(str);
        Assertions.assertEquals("echo $$$$", result);
    }

    @Test
    public void testPreprocessDoubleSubstitutions() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $x";
        Environment.setVariableValue("x", "a$b");
        Environment.setVariableValue("b", "z");
        String result = preprocessor.process(str);
        Assertions.assertEquals("echo a$b", result);
    }

    @Test
    public void testPreprocessMulti() { // important test
        Preprocessor preprocessor = new Preprocessor();
        String str = "echo $p | cat \"sd$x1&\" | echo 'dfjk\"$x1\"dgds\'";
        Environment.setVariableValue("x1", "x2");
        String result = preprocessor.process(str);
        Assertions.assertEquals("echo $p | cat \"sdx2&\" | echo 'dfjk\"$x1\"dgds\'", result);
    }
}
