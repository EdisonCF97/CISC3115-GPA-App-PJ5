import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class TestGpaApp {
    private static Path tmpdir;
        
    @BeforeAll
    public static void setUpTempDir() throws IOException {
        tmpdir = Files.createTempDirectory("test_gpaapp_");
    }

    @Test
    public void testMainExistence() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getStaticMethod(clazz, "main", new Class<?>[] { (new String[] {}).getClass()});        
    }

    @Test
    public void testInitializeStudentExistence() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "initializeStudent", new Class<?>[]{String.class});
    }

    @Test
    public void testDisplayMenuExistence() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "displayMenu", null);        
    }

    @Test
    public void testDisplayGpaExistence() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "displayGpa", new Class<?>[]{double.class});        
    }

    @Test
    public void testDisplayStudentExistence() {
        Class<?> studentClazz = TestUtils.getClass("Student");
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "displayStudent", new Class<?>[]{studentClazz});        
    }


    @Test
    public void testDisplayCourseExistence() {
        Class<?> courseClazz = TestUtils.getClass("Course");
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "displayCourse", new Class<?>[]{courseClazz});        
    }


    @Test
    public void testGetUserOptionExistence() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "getUserOption", new Class<?>[]{Scanner.class});        
    }

    @Test
    public void testGetCourseFromUserExistence() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "getCourseFromUser", new Class<?>[]{Scanner.class});        
    }


    @Test
    public void testInitializeStudentNoDataFile() {
        Class<?> studentClazz = TestUtils.getClass("Student");
        Method numMethod = TestUtils.getMethod(studentClazz, "getNumOfCourses", null);

        Class<?> appClazz = TestUtils.getClass("GpaApp");
        Method initMethod = TestUtils.getMethod(appClazz, "initializeStudent", new Class<?>[]{String.class});
        final Path tmpFile = Paths.get(tmpdir.toString(), "gpaapp_test_nodata.txt");
        
        assertDoesNotThrow(() -> TestUtils.invokeMethod(null, initMethod, new Object[]{tmpFile.toString()}, studentClazz),
            "The method should not throw an exception when given an inaccessible data file, but it did");
        Object studentInstance = TestUtils.invokeMethod(null, initMethod, new Object[]{tmpFile.toString()}, studentClazz);
        
        assertDoesNotThrow(() -> TestUtils.invokeMethod(studentInstance, numMethod, null, Integer.class),
            "Failed to invoke the getNumOfCourses()::Student method, likely initialization error by wrong logic in the constructor");
        
        Object result = TestUtils.invokeMethod(studentInstance, numMethod, null, Integer.class);
        assertTrue(result instanceof Integer, "expected the method to return an integer");
        int actual = (Integer)result;
        assertEquals(0, actual, "expectd 0 courses, but was " + actual);
    }

    @Test
    public void testInitializeStudentDataFile() {
        try {
            Class<?> courseClazz = TestUtils.getClass("Course");

            Class<?> studentClazz = TestUtils.getClass("Student");
            Method numMethod = TestUtils.getMethod(studentClazz, "getNumOfCourses", null);

            Class<?> appClazz = TestUtils.getClass("GpaApp");
            Method initMethod = TestUtils.getMethod(appClazz, "initializeStudent", new Class<?>[]{String.class});
            final Path tmpFile = Paths.get(tmpdir.toString(), "gpaapp_test_data.txt");
            TestUtils.createGoodTestStudentFile(tmpFile, 5, new int[]{1, 2, 3, 4, 5}, new String[]{"A", "B", "C", "D", "F"});
            
            assertDoesNotThrow(() -> TestUtils.invokeMethod(null, initMethod, new Object[]{tmpFile.toString()}, studentClazz),
                "The method should not throw an exception when given an inaccessible data file, but it did");
            Object studentInstance = TestUtils.invokeMethod(null, initMethod, new Object[]{tmpFile.toString()}, studentClazz);
            
            assertDoesNotThrow(() -> TestUtils.invokeMethod(studentInstance, numMethod, null, Integer.class),
                "Failed to invoke the getNumOfCourses()::Student method, likely initialization error by wrong logic in the constructor");
            
            Object result = TestUtils.invokeMethod(studentInstance, numMethod, null, Integer.class);
            assertTrue(result instanceof Integer, "expected the method to return an integer");
            int actual = (Integer)result;
            assertEquals(5, actual, "expectd 5 courses, but was " + actual);


            TestUtils.checkStudentCourses(studentClazz, courseClazz, studentInstance, new int[]{1, 2, 3, 4, 5}, new String[]{"A", "B", "C", "D", "F"});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }  

    
    @Test
    public void testDisplayMenu() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        TestUtils.getMethod(clazz, "displayMenu", null);        
    }


    @AfterAll
    public static void cleanTempDir() {
        boolean result = TestUtils.deleteDirectory(tmpdir.toFile());
        assertTrue(result, "failed to clean up temporary working directory -- error in test setup");
        assertFalse(Files.exists(tmpdir), "Directory still exists -- error in test setup");        
    }    
}
