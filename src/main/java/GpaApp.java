import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class GpaApp {
    public static void main(String[] args) {
        String dataFile = "gpaapp.txt";

        if (args.length > 0) {
            dataFile = args[0];
        }

        try {
            Student student = initializeStudent(dataFile);

            Scanner scanner = new Scanner(System.in);
            int option;

            do {
                displayMenu();
                option = getUserOption(scanner);

                switch (option) {
                    case 1:
                        Course course = getCourseFromUser(scanner);
                        student.addCourse(course);
                        break;
                    case 2:
                        for (int i = 0; i < student.getNumOfCourses(); i++) {
                            displayCourse(student.getCourse(i));
                        }
                        break;
                    case 3:
                        try {
                            double gpa = student.computeGpa();
                            displayGpa(gpa);
                        } catch (NoValidGpaException e) {
                            System.out.println("GPA is unavailable");
                        }
                        break;
                    case 4:
                        try {
                            student.save(dataFile);
                            System.out.println("Data saved successfully.");
                        } catch (IOException e) {
                            System.out.println("Error saving data: " + e.getMessage());
                        }
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Wrong option. Retry.");
                }

            } while (option != 0);

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("Data file not found. Initializing student with no courses.");
        }
    }

   /*  public static Student initializeStudent(String dataFile) throws FileNotFoundException {
        return new Student(dataFile);
    }
*/
    public static Student initializeStudent(String dataFile) {
        try {
            return new Student(dataFile);
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found. Initializing student with no courses.");
            return new Student(); 
        } catch (InvalidDataFileException e) {
            System.out.println("Invalid data file. Initializing student with no courses.");
            return new Student(); 
        }
    }
    

    public static void displayMenu() {
        System.out.println("1. Add Course");
        System.out.println("2. List Courses");
        System.out.println("3. Display GPA");
        System.out.println("4. Save data");
        System.out.println("0. Exit");
    }

    public static int getUserOption(Scanner scanner) {
        int option;
        do {
            System.out.print("Enter Option: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Wrong option. Retry.");
                scanner.next();
            }
            option = scanner.nextInt();
        } while (option < 0 || option > 4);

        return option;
    }

    public static void displayGpa(double gpa) {
        if (gpa >= 0) {
            System.out.printf("GPA is %.3f%n", gpa);
        } else {
            System.out.println("GPA is unavailable");
        }
    }

    public static Course getCourseFromUser(Scanner scanner) {
        System.out.print("Enter credits: ");
        int credits = scanner.nextInt();
        System.out.print("Enter grade: ");
        String grade = scanner.next();

        return new Course(credits, grade);
    }

    public static void displayCourse(Course course) {
        System.out.printf("%8d%6s%7.3f%n", course.getCredits(), course.getGrade(), GradeUtils.getPoints(course.getGrade()));
    }

    public static void displayStudent(Student student) {
        if (student.getNumOfCourses() == 0) {
            System.out.println("No courses taken");
        } else {
            System.out.println("Credits Grade Points");
            for (int i = 0; i < student.getNumOfCourses(); i++) {
                displayCourse(student.getCourse(i));
            }
        }
    }
}
