import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGpaAppUI {
    public final static String MENU_TEXT = "1. Add Course" + System.getProperty("line.separator") +
        "2. List Courses" + System.getProperty("line.separator") +
        "3. Display GPA" + System.getProperty("line.separator") + 
        "4. Save data" + System.getProperty("line.separator") + 
        "0. Exit" + System.getProperty("line.separator"); 
    public final static String GPA_TEXT_FMT = "GPA is %5.3f\n";
    private final String GPA_TEXT_NA = "GPA is unavailable\n";
    public  final static String OPTION_PROMPT = "Enter Option: ";
    private final String BAD_OPTION_PROMPT = "Wrong option. Retry." + System.getProperty("line.separator");
    private final String COURSE_INPUT_FMT = "%d" + System.getProperty("line.separator") + 
                                            "%s" + System.getProperty("line.separator"); 
    public final static String COURSE_PROMPT = "Enter credits: Enter grade: ";
    public final static String COURSE_DISPLAY_FMT = "%8d%6s%7.3f\n";
    public final static String STUDENT_DISPLAY_HEADER = String.format("%8s%6s%7s\n", "Credits", "Grade", "Points");
    public final static String STUDNET_NO_COURSES = "No courses taken" + System.getProperty("line.separator");
    
    
    @Test
    public void testDisplayMenu() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        Method method = TestUtils.getMethod(clazz, "displayMenu", null);
       
        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        assertDoesNotThrow(() -> TestUtils.invokeMethod(null, method, null, null));

        String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));

        assertEquals(MENU_TEXT, actual, "displayed menu does not meet expectation.");


        System.setOut(originalOut);
    }

    @Test
    public void testGetUserOptionValidOption() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        Method method = TestUtils.getMethod(clazz, "getUserOption", new Class<?>[]{Scanner.class});

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        System.setOut(new PrintStream(bos));

        String expected = OPTION_PROMPT.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
        for (int i=0; i<=4; i++) {
            String userInput = Integer.toString(i) + System.getProperty("line.separator");
            final ByteArrayInputStream bis = new ByteArrayInputStream(userInput.getBytes());

            assertDoesNotThrow(() -> 
                TestUtils.invokeMethod(null, method, new Object[]{new Scanner(bis)}, Integer.class),
                "expected no expection thrown");

            String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            assertEquals(expected, actual, "Prompt for user option isn't expected");

            bos.reset();
        }

        System.setOut(originalOut);
    }


    @Test
    public void testGetUserOptionInvalidOption() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        Method method = TestUtils.getMethod(clazz, "getUserOption", new Class<?>[]{Scanner.class});

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        for (String userInput: new String[] {
            "5" + System.getProperty("line.separator") + "1"  + System.getProperty("line.separator"),
            "-1" + System.getProperty("line.separator") + "2"  + System.getProperty("line.separator"),
            "5" + System.getProperty("line.separator") + "3"  + System.getProperty("line.separator"),
            "-1" + System.getProperty("line.separator") + "4"  + System.getProperty("line.separator"),
            "5" + System.getProperty("line.separator") + "0"  + System.getProperty("line.separator"),
            "5\n6\n7\n8\n9\n1\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator"))
        }) {
            String expected = (OPTION_PROMPT + BAD_OPTION_PROMPT + OPTION_PROMPT).replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            if (userInput.length() > 10) {
                expected = (OPTION_PROMPT + BAD_OPTION_PROMPT + 
                            OPTION_PROMPT + BAD_OPTION_PROMPT +
                            OPTION_PROMPT + BAD_OPTION_PROMPT +
                            OPTION_PROMPT + BAD_OPTION_PROMPT +
                            OPTION_PROMPT + BAD_OPTION_PROMPT + OPTION_PROMPT).replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            }
            final ByteArrayInputStream bis = new ByteArrayInputStream(userInput.getBytes());

            assertDoesNotThrow(() -> 
                TestUtils.invokeMethod(null, method, new Object[]{new Scanner(bis)}, Integer.class),
                "expected no expection thrown");

            String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            assertEquals(expected, actual, "Prompt for user option isn't expected");
            bos.reset();
        }

        System.setOut(originalOut);
    }


    @Test
    public void testDisplayGpa() {
        Class<?> clazz = TestUtils.getClass("GpaApp");
        Method method = TestUtils.getMethod(clazz, "displayGpa", new Class<?>[]{double.class});
       
        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        
        for (double gpa: new double[] {4.0, 3.0, 3.12, 3.123, 3.1234, 3.1235, -2.0}) {
            assertDoesNotThrow(() -> TestUtils.invokeMethod(null, method, new Object[]{gpa}, null),
            "expected no expection thrown");

            String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            String expected;
            if (gpa >= 0) {
                expected = String.format(GPA_TEXT_FMT, gpa).replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            } else {
                expected = GPA_TEXT_NA.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            }
            
            assertEquals(expected, actual, "Unexpected GPA format");

            bos.reset();
        }
        System.setOut(originalOut);
    }  
    
    
    @Test
    public void testGetCourseFromUser() {
        Class<?> courseClazz = TestUtils.getClass("Course");

        Class<?> clazz = TestUtils.getClass("GpaApp");
        Method method = TestUtils.getMethod(clazz, "getCourseFromUser", new Class<?>[]{Scanner.class});

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        int[] credits = {1, 2, 3, 4, 5};
        String[] grades = {"A", "B", "C", "D", "F"};

        for (int i=0; i < 5; i ++) {
            final ByteArrayInputStream bis = new ByteArrayInputStream(String.format(COURSE_INPUT_FMT, credits[i], grades[i]).getBytes());
            assertDoesNotThrow(() -> 
                TestUtils.invokeMethod(null, method, new Object[]{new Scanner(bis)}, courseClazz),
                "expected no expection thrown");

            String actual = bos.toString();
            assertEquals(COURSE_PROMPT, actual, "Prompt not expected");
            bis.reset();
            Object resultCourse = TestUtils.invokeMethod(null, method, new Object[]{new Scanner(bis)}, courseClazz);
            TestUtils.checkCourse(clazz, courseClazz, credits[i], grades[i], resultCourse);
            bos.reset();
        }

        System.setOut(originalOut);
    }


    @Test
    public void testDisplayCourse() {
        Class<?> courseClazz = TestUtils.getClass("Course");
        Object courseInstance = TestUtils.getInstance(courseClazz, new Class<?>[]{int.class, String.class}, new Object[]{3, "A"});

        Class<?> gpaAppClazz = TestUtils.getClass("GpaApp");
        Method dcMethod = TestUtils.getMethod(gpaAppClazz, "displayCourse", new Class<?>[]{courseClazz});

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        assertDoesNotThrow(() -> TestUtils.invokeMethod(null, dcMethod, new Object[]{courseInstance}, null),
            "expect no exception thrown");
        String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
        String expected = String.format(COURSE_DISPLAY_FMT, 3, "A", 4.0)
                                .replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
        assertEquals(expected, actual, "course is displayed in an unexpected format");

        System.setOut(originalOut);
    }

    @Test
    public void testDisplayStudentWithCourses() {
        int[] credits = new int[] {1, 2, 3, 4, 5};
        String[] grades = new String[]{"A", "B", "C", "D", "F"};
        double[] points = new double[]{4.0, 3.0, 2.0, 1.0, 0.0};

        Class<?> studentClazz = TestUtils.getClass("Student");
        Class<?> courseClazz = TestUtils.getClass("Course");
        Class<?> gpaAppClazz = TestUtils.getClass("GpaApp");
        Method dsMethod = TestUtils.getMethod(gpaAppClazz, "displayStudent", new Class<?>[]{studentClazz});

        Object studentInstance = TestUtils.getInstance(studentClazz, null, null);
        TestUtils.addCourses(studentClazz, studentInstance, courseClazz, new int[] {1, 2, 3, 4, 5}, new String[]{"A", "B", "C", "D", "F"});

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        assertDoesNotThrow(() -> TestUtils.invokeMethod(null, dsMethod, new Object[]{studentInstance}, null),
            "expected no exceptions thrown");

        String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));;

        assertTrue(actual.contains(STUDENT_DISPLAY_HEADER.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"))),
            "expected student display contains <" + STUDENT_DISPLAY_HEADER + ">, but was <" + actual + ">");
        for (int i=0; i<5; i++) {
            String courseDisplay = String.format(COURSE_DISPLAY_FMT, credits[i], grades[i], points[i])
                                         .replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
            assertTrue(actual.contains(courseDisplay),
                "expected student display contains <" + courseDisplay + ">, but was <" + actual + ">");
        }

        System.setOut(originalOut);
    }

    @Test
    public void testDisplayStudentNoCourses() {
        Class<?> studentClazz = TestUtils.getClass("Student");
        Class<?> gpaAppClazz = TestUtils.getClass("GpaApp");
        Method dsMethod = TestUtils.getMethod(gpaAppClazz, "displayStudent", new Class<?>[]{studentClazz});

        Object studentInstance = TestUtils.getInstance(studentClazz, null, null);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        assertDoesNotThrow(() -> TestUtils.invokeMethod(null, dsMethod, new Object[]{studentInstance}, null),
            "expected no exceptions thrown");

        String actual = bos.toString().replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));;

        assertTrue(actual.contains(STUDNET_NO_COURSES.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"))),
            "expected student display contains <" + STUDNET_NO_COURSES + ">, but was <" + actual + ">");

        System.setOut(originalOut);
    }    
}
