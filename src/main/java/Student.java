import java.io.*;
import java.util.ArrayList;

public class Student {
    private ArrayList<Course> courses;

    public Student() {
        courses = new ArrayList<>();
    }

    public Student(String dataFile) throws InvalidDataFileException {
        courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 2) {
                    throw new InvalidDataFileException("Invalid data format in file: " + dataFile);
                }
                try {
                    int credits = Integer.parseInt(fields[0]);
                    String grade = fields[1];
                    Course course = new Course(credits, grade);
                    courses.add(course);
                } catch (NumberFormatException e) {
                    throw new InvalidDataFileException("Invalid data format in file: " + dataFile);
                } catch (IllegalArgumentException e) {
                    throw new InvalidDataFileException("Invalid data format in file: " + dataFile);
                }
            }
        } catch (IOException e) {
            throw new InvalidDataFileException("Error reading file: " + dataFile);
        }
    }

    public int getNumOfCourses() {
        return courses.size();
    }

    public Course getCourse(int index) {
        if (index < 0 || index >= courses.size()) {
            throw new IllegalArgumentException("Invalid index");
        }
        return new Course(courses.get(index));
    }

    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        courses.add(new Course(course));
    }

    public double computeGpa() throws NoValidGpaException {
        if (courses.isEmpty()) {
            throw new NoValidGpaException("No courses added");
        }

        int totalCredits = 0;
        double totalPoints = 0.0;

        for (Course course : courses) {
            totalCredits += course.getCredits();
            totalPoints += GradeUtils.getPoints(course.getGrade()) * course.getCredits();
        }

        if (totalCredits == 0) {
            throw new NoValidGpaException("Total credits is 0");
        }

        return totalPoints / totalCredits;
    }

    public void save(String dataFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
            for (Course course : courses) {
                writer.println(course.getCredits() + "," + course.getGrade());
            }
        }
    }
}
