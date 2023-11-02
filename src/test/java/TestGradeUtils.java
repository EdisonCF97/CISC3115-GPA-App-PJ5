import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class TestGradeUtils {
    @Test
    public void testClassExistence() {
        TestUtils.getClass("GradeUtils");
    }

    @Test
    public void testGetPointsExistence() {
        Class<?> clazz = TestUtils.getClass("GradeUtils");
        TestUtils.getStaticMethod(clazz, "getPoints", new Class<?>[]{String.class});
    }

    @Test
    public void testGetPointsMethod() {
        Class<?> clazz = TestUtils.getClass("GradeUtils");
        Method method = TestUtils.getStaticMethod(clazz, "getPoints", new Class<?>[]{String.class});

        try {
            String[] gradeList = {"A", "B", "C", "D", "F"};
            double[] pointsList = {4.0, 3.0, 2.0, 1.0, 0.0};
            for (int i=0; i<gradeList.length; i++) {
                String grade = gradeList[i];
                double points = pointsList[i];
                Object object = TestUtils.invokeMethod(null, method, new Object[] {grade}, Double.class);
                assertEquals(points, ((Double)object).doubleValue(),
                    "Letter grade " + grade + " is " +  ((Double)object).doubleValue() + 
                    " but " + points + " is expected");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
