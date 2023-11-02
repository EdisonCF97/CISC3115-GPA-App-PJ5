import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class TestCourse {
    @Test
    public void testClassExistance() {
        TestUtils.getClass("Course");        
    }

    @Test
    public void testCtorExistance() {
        Class<?> clazz = TestUtils.getClass("Course");
        TestUtils.getConstructor(clazz, new Class<?>[]{int.class, String.class});
        TestUtils.getConstructor(clazz, new Class<?>[]{clazz});
    }

    @Test
    public void testGetCreditsMethod() {
        int expected = 4;
        Class<?> clazz = TestUtils.getClass("Course");
        Object instance = TestUtils.getInstance(clazz, new Class<?>[]{int.class, String.class}, new Object[]{expected, "B"});
        Method method = TestUtils.getMethod(clazz, "getCredits", null);
        
        Object result = TestUtils.invokeMethod(instance, method, null, Integer.class);
        int actual = (Integer)result;
        assertEquals(expected, actual, 
            "Exptected getCredits() to return " + expected + ", but got " + actual);        
    }

    @Test
    public void testGetGradeMethod() {
        String expected = "A";

        Class<?> clazz = TestUtils.getClass("Course");
        Object instance = TestUtils.getInstance(clazz, new Class<?>[]{int.class, String.class}, new Object[]{3, expected});
        Method method = TestUtils.getMethod(clazz, "getGrade", null);
        
        Object result = TestUtils.invokeMethod(instance, method, null, String.class);
        String actual = (String)result;

        assertEquals(expected, actual, 
            "Exptected getGrade() to return " + expected + ", but got " + actual);     
    }

}
