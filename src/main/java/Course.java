public class Course {
    private int credits;
    private String grade;

    public Course(int credits, String grade) {
        this.credits = credits;
        this.grade = grade;
    }

    public Course(Course course) {
        this.credits = course.getCredits();
        this.grade = course.getGrade();
    }

    public int getCredits() {
        return credits;
    }

    public String getGrade() {
        return grade;
    }

}