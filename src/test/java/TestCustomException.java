import org.junit.jupiter.api.Test;

public class TestCustomException {
    @Test
    public void testInvalidDataFileExceptionClassExistence() {
        TestUtils.getClass("InvalidDataFileException");
    }

    @Test
    public void testInvalidDataFileExceptionCtorExistence() {
        Class<?> clazz = TestUtils.getClass("InvalidDataFileException");
        TestUtils.getConstructor(clazz, new Class<?>[]{String.class});
    }

    @Test
    public void testNoValidGpaExceptionClassExistence() {
        TestUtils.getClass("NoValidGpaException");
    }

    @Test
    public void testNoValidGpaExceptionCtorExistence() {
        Class<?> clazz = TestUtils.getClass("InvalidDataFileException");
        TestUtils.getConstructor(clazz, new Class<?>[]{String.class});
    }

    @Test
    public void testUnsupportedGradeExceptionClassExistence() {
        TestUtils.getClass("UnsupportedGradeException");
    }


    @Test
    public void testUnsupportedGradeExceptionCtorExistence() {
        Class<?> clazz = TestUtils.getClass("UnsupportedGradeException");
        TestUtils.getConstructor(clazz, new Class<?>[]{String.class});
    }
}
