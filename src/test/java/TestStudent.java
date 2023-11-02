import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestStudent {
    private static Path tmpdir;

    @BeforeAll
    public static void setUpTempDir() throws IOException {
        tmpdir = Files.createTempDirectory("test_student_");
    }

    @Test
    public void testClassExistence() {
        TestUtils.getClass("Student");
    }

    @Test
    public void testCtorExistence() {
        Class<?> clazz = TestUtils.getClass("Student");
        TestUtils.getConstructor(clazz, (Class<?>[]) null);
        TestUtils.getConstructor(clazz, new Class<?>[] {String.class});
    }

    @Test
    public void testCtorNoArgs() {
        Class<?> clazz = TestUtils.getClass("Student");
        assertDoesNotThrow(() -> TestUtils.getInstance(clazz, null, null),
        "Failed to construct Student instance");        
    }

    @Test
    public void testCtorGoodFile() {
        try {
            final Path tmpFile = Paths.get(tmpdir.toString(), "student_test_good_data_0.txt");

            Class<?> clazz = TestUtils.getClass("Student");
            TestUtils.createGoodTestStudentFile(tmpFile, 0);
            assertDoesNotThrow(() -> TestUtils.getInstance(clazz, new Class<?>[]{String.class}, new Object[] {tmpFile.toString()}),
                "Failed to construct Student instance from an empty file");
            for (int i=1; i<=100; i++) {
                final Path tmpFileVaringSize = Paths.get(tmpdir.toString(), "student_test_good_data_" + i + ".txt");
                TestUtils.createGoodTestStudentFile(tmpFileVaringSize, i);
                assertDoesNotThrow(() -> TestUtils.getInstance(clazz, new Class<?>[]{String.class}, new Object[] {tmpFileVaringSize.toString()}),
                    "Failed to construct Student instance from a Student data file with " + i + " records");                
            }
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }


    private void testCtorBadFile(int badType, String badExplain) {
        try {
            Path tmpFile = Paths.get(tmpdir.toString(), "student_test_bad_data_type_" + badType + ".txt");
            Class<?> exceptionClazz = TestUtils.getClass("InvalidDataFileException");

            Class<?> clazz = TestUtils.getClass("Student");
            TestUtils.createBadTestStudentFile(tmpFile, badType);
            Exception exception = assertThrows(Exception.class, () -> TestUtils.getInstance(clazz, new Class<?>[]{String.class}, new Object[] {tmpFile.toString()}),
                "Exception expected when constructing Student instance given " + badExplain);
            assertTrue(exception.getCause() instanceof InvocationTargetException, "expected to throw a custom exception");
            String actualMessage = exception.getCause().getCause().getMessage();
            assertTrue(exceptionClazz.isAssignableFrom(exception.getCause().getCause().getClass()),
                "expected Student constructor throws an exception of " + exceptionClazz + 
                " but was " + exception.getCause().getCause() + " with message <" + actualMessage + ">");
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Test
    public void testCtorBadFileNonCSV() {
        testCtorBadFile(TestUtils.BAD_STUDENT_FILE_NOT_CSV, "a non-CSV student data file");
    }

    @Test
    public void testCtorBadFileExtraField() {
        testCtorBadFile(TestUtils.BAD_STUDENT_FILE_EXTRA_FIELD, "a student data file with extra fields");
    }

    
    @Test
    public void testCtorBadFileFewerField() {
        testCtorBadFile(TestUtils.BAD_STUDENT_FILE_FEWER_FIELD, "a student data file with fewer fields");
    }

    @Test
    public void testGetNumOfCoursesExistence() {
        Class<?> clazz = TestUtils.getClass("Student");
        TestUtils.getMethod(clazz, "getNumOfCourses", null);
    }

    @Test
    public void testGetNumOfCoursesZeroCourses() {
        Class<?> clazz = TestUtils.getClass("Student");
        Object instance = TestUtils.getInstance(clazz, null, null);
        Method method = TestUtils.getMethod(clazz, "getNumOfCourses", null);
        assertDoesNotThrow(() -> TestUtils.invokeMethod(instance, method, null, Integer.class),
            "Failed to invoke the getNumOfCourses()::Student method, likely initialization error by wrong logic in the constructor");
        Object result = TestUtils.invokeMethod(instance, method, null, Integer.class);
        assertTrue(result instanceof Integer, "expected the method to return an integer");
        int actual = (Integer)result;
        int expected = 0;
        assertEquals(expected, actual, "expectd 0 courses when no courses entered, but was " + actual);
    }

    @Test
    public void testGetNumOfCoursesNCourses() {
        try {
            Class<?> clazz = TestUtils.getClass("Student");
            for (int i=0; i<=100; i++) {
                final Path tmpFileVaringSize = Paths.get(tmpdir.toString(), "student_test_good_data_num_" + i + ".txt");
                TestUtils.createGoodTestStudentFile(tmpFileVaringSize, i);
                Object instance = TestUtils.getInstance(clazz, new Class<?>[]{String.class}, new Object[] {tmpFileVaringSize.toString()});
                Method method = TestUtils.getMethod(clazz, "getNumOfCourses", null);
                assertDoesNotThrow(() -> TestUtils.invokeMethod(instance, method, null, Integer.class),
                    "Failed to invoke the getNumOfCourses()::Student method, likely initialization error by wrong logic in the constructor");
                Object result = TestUtils.invokeMethod(instance, method, null, Integer.class);
                assertTrue(result instanceof Integer, "expected the method to return an integer");
                int actual = (Integer)result;
                assertEquals(i, actual, "expectd " + i + "courses, but was " + actual);
            }
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetCourseExistence() {
        Class<?> clazz = TestUtils.getClass("Student");
        TestUtils.getMethod(clazz, "getCourse", new Class<?>[]{int.class});
    }

    @Test
    public void testGetCourse() {
        try {
            int[] credits = new int[] {1, 2, 3, 4, 5};
            String[] grades = new String[] {"A", "B", "C", "D", "F"};

            Class<?> courseClazz = TestUtils.getClass("Course");
            Class<?> studentClazz = TestUtils.getClass("Student");
            final Path tmpFileVaringSize = Paths.get(tmpdir.toString(), "student_test_get_course.txt");
            TestUtils.createGoodTestStudentFile(tmpFileVaringSize, 5, credits, grades);
            Object studentInstance = TestUtils.getInstance(studentClazz, new Class<?>[]{String.class}, new Object[] {tmpFileVaringSize.toString()});

            TestUtils.checkStudentCourses(studentClazz, courseClazz, studentInstance, credits, grades);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testAddCourseExistence() {
        Class<?> clazz = TestUtils.getClass("Student");
        Class<?> courseClazz = TestUtils.getClass("Course");
        TestUtils.getMethod(clazz, "addCourse", new Class<?>[]{courseClazz});
    }

    @Test
    public void testAddCourse() {
        int[] credits = new int[] {1, 2, 3, 4, 5};
        String[] grades = new String[] {"A", "B", "C", "D", "F"};

        Class<?> courseClazz = TestUtils.getClass("Course");
        Class<?> studentClazz = TestUtils.getClass("Student");
        Object studentInstance = TestUtils.getInstance(studentClazz, null, null);

        TestUtils.addCourses(studentClazz, studentInstance,courseClazz, credits, grades);

        TestUtils.checkStudentCourses(studentClazz, courseClazz, studentInstance, credits, grades);      
    }


    @Test
    public void testComputeGpaExistence() {
        Class<?> clazz = TestUtils.getClass("Student");
        TestUtils.getMethod(clazz, "computeGpa", null);
    }

    @Test
    public void testComputeGpa() {
        int[] credits = new int[] {1, 2, 3, 4, 5};
        String[] grades = new String[] {"A", "B", "C", "D", "F"};

        Class<?> courseClazz = TestUtils.getClass("Course");
        Class<?> studentClazz = TestUtils.getClass("Student");
        Object studentInstance = TestUtils.getInstance(studentClazz, null, null);

        TestUtils.addCourses(studentClazz, studentInstance, courseClazz, credits, grades);
        
        double expectedGpa = (4.*1 + 3.*2 + 2.*3 + 1.*4 + 0.*5)/(1 + 2 + 3 + 4 + 5);
        
        Method method = TestUtils.getMethod(studentClazz, "computeGpa", null);
        Object result = TestUtils.invokeMethod(studentInstance, method, null, Double.class);
        double actualGpa = (Double)result;
        assertEquals(expectedGpa, actualGpa, 
            "expected GPA " + expectedGpa + " but was " + actualGpa);
    }


    @Test
    public void testSaveExistence() {
        Class<?> clazz = TestUtils.getClass("Student");
        TestUtils.getMethod(clazz, "save", new Class<?>[]{String.class});
    }

    @Test
    public void testSave() {
        int[] credits = new int[] {1, 2, 3, 4, 5};
        String[] grades = new String[] {"A", "B", "C", "D", "F"};

        Class<?> courseClazz = TestUtils.getClass("Course");
        Class<?> studentClazz = TestUtils.getClass("Student");
        Object studentInstance1 = TestUtils.getInstance(studentClazz, null, null);

        TestUtils.addCourses(studentClazz, studentInstance1,courseClazz, credits, grades);

        Method saveMethod = TestUtils.getMethod(studentClazz, "save", new Class<?>[]{String.class});
        final Path tmpFile = Paths.get(tmpdir.toString(), "student_test_data_saved.txt");
        TestUtils.invokeMethod(studentInstance1, saveMethod, new Object[]{tmpFile.toString()}, null);

        Object studentInstance2 = TestUtils.getInstance(studentClazz, 
            new Class<?>[]{String.class}, new Object[] {tmpFile.toString()});
        TestUtils.checkStudentCourses(studentClazz, courseClazz, studentInstance2, credits, grades);
    }

    @AfterAll
    public static void cleanTempDir() {
        boolean result = TestUtils.deleteDirectory(tmpdir.toFile());
        assertTrue(result, "failed to clean up temporary working directory -- error in test setup");
        assertFalse(Files.exists(tmpdir), "Directory still exists -- error in test setup");        
    }
}
