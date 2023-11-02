import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public class TestUtils {
    public static final int BAD_STUDENT_FILE_NOT_CSV = 1, 
                            BAD_STUDENT_FILE_EXTRA_FIELD = 2,
                            BAD_STUDENT_FILE_FEWER_FIELD = 3;

    
    public static Class<?> getClass(String className) {
        assertDoesNotThrow(() -> Class.forName(className), "Class " + className + " not found");
        try {
            Class<?> clazz = Class.forName(className);
            return clazz;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>[] paraClazz) {
        try {
            assertDoesNotThrow(() -> clazz.getConstructor(paraClazz),
                "Constructor for " + clazz + " with formal parameters " + Arrays.toString(paraClazz) + " not found");
      
            Constructor<?> ctor = clazz.getConstructor(paraClazz);
            assertNotNull(ctor);
            return ctor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] args) {
        try {
            assertDoesNotThrow(() -> clazz.getMethod(methodName, args),
                "Method " + methodName + " not found");
            Method method = clazz.getMethod(methodName, args);
            return method;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    } 


    public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>[] args) {
        Method method = getMethod(clazz, methodName, args);
        assertTrue(Modifier.isStatic(method.getModifiers()), 
            "expected getPoints(grade:String)::GradeUtils to be a static/class method");
        return method;
    }

    public static Object invokeMethod(Object object, Method method, Object[] args, Class<?> returnType) {
        try {
            Object result = method.invoke(object, args);
            if (returnType != null) {
                assertTrue(result.getClass().isAssignableFrom(returnType),
                    "Expected a " + returnType + " value, but got a different type " + result.getClass());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }  
    
    
    public static Object getInstance(Class<?> clazz, Class<?>[] paraClazz, Object[] args) {
        try {
            assertDoesNotThrow(() -> clazz.getConstructor(paraClazz),
                "Constructor Coure(credits:int, grade:String)::Course not found");
            Constructor<?> ctor = clazz.getConstructor(paraClazz);
            Object instance = ctor.newInstance(args);
            assertNotNull(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createGoodTestStudentFile(Path path, int size, int[] credits, String[] grades) throws IOException {
        try (PrintWriter out = new PrintWriter(new File(path.toString()), StandardCharsets.UTF_8)) {
            for (int i=0; i<size; i++) {
                int c = credits[i];
                String g = grades[i];
                out.printf("%d,%s\n", c, g);
            }
        }
    }
    
    public static void createGoodTestStudentFile(Path path, int size) throws IOException {
        int[] credits = {1, 2, 3, 4, 5};
        String[] grades = {"A", "B", "C", "D", "F"};
        Random rng = new Random();
        try (PrintWriter out = new PrintWriter(new File(path.toString()), StandardCharsets.UTF_8)) {
            for (int i=0; i<size; i++) {
                int c = credits[rng.nextInt(credits.length)];
                String g = grades[rng.nextInt(grades.length)];
                out.printf("%d,%s\n", c, g);
            }
        }
    }

    public static void createBadTestStudentFile(Path path, int badType) throws IOException {
        int[] credits = {1, 2, 3, 4, 5};
        String[] grades = {"A", "B", "C", "D", "F"};
        Random rng = new Random();
        try (PrintWriter out = new PrintWriter(new File(path.toString()), StandardCharsets.UTF_8)) {
            switch(badType) {
                case BAD_STUDENT_FILE_NOT_CSV:
                    for (int i=0; i<5; i++) {
                        int c = credits[rng.nextInt(credits.length)];
                        String g = grades[rng.nextInt(grades.length)];
                        out.printf("%d %s\n", c, g);
                    }
                    break;
                case BAD_STUDENT_FILE_EXTRA_FIELD:
                    for (int i=0; i<5; i++) {
                        int c = credits[rng.nextInt(credits.length)];
                        String g = grades[rng.nextInt(grades.length)];
                        out.printf("%d,%s,4.0\n", c, g);
                    }
                    break; 
                case BAD_STUDENT_FILE_FEWER_FIELD:
                    for (int i=0; i<5; i++) {
                        int c = credits[rng.nextInt(credits.length)];
                        out.printf("%d\n", c);
                    }
                    break; 
                default:
                    throw new IllegalArgumentException("Unsupported bad student file type " + badType);
            }
        }
    }

    
    public static void addCourses(Class<?> studentClazz, Object studentInstance, Class<?> courseClazz, int[] credits, String[] grades) {
        Method method = TestUtils.getMethod(studentClazz, "addCourse", new Class<?>[]{courseClazz});
        for (int i=0; i<5; i++) {
            Object courseInstance = TestUtils.getInstance(courseClazz, 
                new Class<?>[] {int.class, String.class}, new Object[]{credits[i], grades[i]});
            TestUtils.invokeMethod(studentInstance, method, new Object[]{courseInstance}, null);
        }
    }


    public static void checkCourse(Class<?> studentClazz, Class<?> courseClazz, int expectedCredits, String expectedGrade, Object resultCourse) {
        Method getCourseCreditsMethod = TestUtils.getMethod(courseClazz, "getCredits", null);
        Method getCourseGradeMethod = TestUtils.getMethod(courseClazz, "getGrade", null);
        Object resultCredits = TestUtils.invokeMethod(resultCourse, getCourseCreditsMethod, null, Integer.class);
        assertEquals(expectedCredits, (Integer)resultCredits,
            "expected course credits to be " + expectedCredits + ", but was " + (Integer)resultCredits);
        Object resultGrade = TestUtils.invokeMethod(resultCourse, getCourseGradeMethod, null, String.class);
        assertEquals(expectedGrade, (String)resultGrade,
            "expected course grade to be " + expectedGrade + ", but was " + (String)resultGrade);
    }
   
    public static void checkStudentCourses(Class<?> studentClazz, Class<?> courseClazz, Object studentInstance, int[] credits, String[] grades) {
        Method getCourseMethod = TestUtils.getMethod(studentClazz, "getCourse", new Class<?>[]{int.class});
        for (int i=0; i<Math.min(credits.length, grades.length); i++) {
            Object resultCourse = TestUtils.invokeMethod(studentInstance, getCourseMethod, new Object[]{i}, courseClazz);
            checkCourse(studentClazz, courseClazz, credits[i], grades[i], resultCourse);
        }        
    }


    // credit & attribution: copied from https://www.baeldung.com/java-delete-directory
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
