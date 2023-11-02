import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TestIntegration {
    private final static String INPUT_2 = "2" + System.getProperty("line.separator");
    private final static String INPUT_3 = "3" + System.getProperty("line.separator");
    private final static String INPUT_4 = "4" + System.getProperty("line.separator");
    private final static String INPUT_14A234 = String.join(System.getProperty("line.separator"), new String[] {"1", "4", "A", "2", "3", "4"});
    private final static String INPUT_14A11B234 = String.join(System.getProperty("line.separator"), new String[] {"1", "4", "A", "1", "1", "B", "2", "3", "4"});
    private final static String NO_GPA = "The student has no valid GPA" + System.getProperty("line.separator");
    private final static String FILE_SAVED_TO = "Data saved to ";
    
    private static Path tmpdir;

    @BeforeAll
    public static void setUpTempDir() throws IOException {
        tmpdir = Files.createTempDirectory("test_integration_");
    }



    @Test
    public void testMain20() {
        String exected = TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + TestGpaAppUI.STUDNET_NO_COURSES + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT;
        testIntegration(INPUT_2, exected);
    }

    @Test
    public void testMain30() {
        String exected = TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + NO_GPA + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT;
        testIntegration(INPUT_3, exected);
    }

    @Test
    public void testMain40() {
        String testDataFile = Paths.get(tmpdir.toString(), "test_gpaapp.txt").toString();
        String exected = TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         FILE_SAVED_TO + testDataFile + System.getProperty("line.separator") +
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT;
        testIntegration(INPUT_4, exected, testDataFile);
        checkEmptyFile(testDataFile);
    }

    @Test
    public void testMain14A234() {
        String testDataFile = Paths.get(tmpdir.toString(), "test_gpaapp_14A234.txt").toString();
        
        String exected = TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         TestGpaAppUI.COURSE_PROMPT + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         TestGpaAppUI.STUDENT_DISPLAY_HEADER + 
                         String.format(TestGpaAppUI.COURSE_DISPLAY_FMT, 4, "A", 4.0) + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         String.format(TestGpaAppUI.GPA_TEXT_FMT, 4.0) + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         FILE_SAVED_TO + testDataFile + System.getProperty("line.separator") +
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT;
        testIntegration(INPUT_14A234, exected, testDataFile);
        checkNonEmptyFile(testDataFile);
    }

    @Test
    public void testMain14A11B234() {
        String testDataFile = Paths.get(tmpdir.toString(), "test_gpaapp_14A11B234.txt").toString();
        
        String exected = TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         TestGpaAppUI.COURSE_PROMPT + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         TestGpaAppUI.COURSE_PROMPT +
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         TestGpaAppUI.STUDENT_DISPLAY_HEADER + 
                         String.format(TestGpaAppUI.COURSE_DISPLAY_FMT, 4, "A", 4.0) + 
                         String.format(TestGpaAppUI.COURSE_DISPLAY_FMT, 1, "B", 3.0) + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         String.format(TestGpaAppUI.GPA_TEXT_FMT, 3.8) + 
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT + 
                         FILE_SAVED_TO + testDataFile + System.getProperty("line.separator") +
                         TestGpaAppUI.MENU_TEXT + TestGpaAppUI.OPTION_PROMPT;
        testIntegration(INPUT_14A11B234, exected, testDataFile);
        checkNonEmptyFile(testDataFile);
    }

    private void checkEmptyFile(String dataFileName) {
        File dataFile = new File(dataFileName);
        assertTrue(dataFile.exists(), "expected to create file " + dataFileName);
        assertEquals(0, dataFile.length(), "expected data file " + dataFileName + " is of length 0, but was " + dataFile.length());
    }

    private void checkNonEmptyFile(String dataFileName) {
        File dataFile = new File(dataFileName);
        assertTrue(dataFile.exists(), "expected to create file " + dataFileName);
        assertTrue(dataFile.length() > 0, "expected non-empty data file " + dataFileName + " but was " + dataFile.length());
    }


    private void testIntegration(String input, String expected, String...varargs) {
        Class<?> gpaAppClazz = TestUtils.getClass("GpaApp");
        Method mainMethod = TestUtils.getMethod(gpaAppClazz, "main", new Class<?>[]{String[].class});
        
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes());
        
        System.setOut(new PrintStream(bos));
        System.setIn(bis);

        if (varargs.length > 0) {
            assertThrows(RuntimeException.class, () -> TestUtils.invokeMethod(null, mainMethod, new Object[] {varargs}, null),
                "expected exception RuntimeException to be thrown");
        } else {
            assertThrows(RuntimeException.class, () -> TestUtils.invokeMethod(null, mainMethod, new Object[] {new String[]{"GpaApp"}}, null),
                "expected exception RuntimeException to be thrown");
        }
        
        expected = expected.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
        String actual = bos.toString()
                           .replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));

        assertEquals(expected, actual, "unexpected output");

        System.setIn(originalIn);
        System.setOut(originalOut);
    }


    @AfterAll
    public static void cleanTempDir() {
        boolean result = TestUtils.deleteDirectory(tmpdir.toFile());
        assertTrue(result, "failed to clean up temporary working directory -- error in test setup");
        assertFalse(Files.exists(tmpdir), "Directory still exists -- error in test setup");        
    }
}
