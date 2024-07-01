package za.co.cas;

/**
 * The Student class represents a student with a name, ID, and grades.
 * It implements Runnable to display the student's information in a separate frame.
 */
public class Student implements Runnable{
    private String name;       // Student's name
    private String id;         // Student's ID
    private Grade grades;      // Student's grades
    private boolean done;      // Indicates if the student's session is completed

    /**
     * Constructor to initialize a student with a full name.
     * @param fullName The full name of the student.
     */
    public Student(String fullName) {
        this.name = fullName;
        this.id = null;       // ID is initially null
        this.grades = new Grade(); // Initialize grades
        this.done = false;    // Initially not done
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grade getGrades() {
        return grades;
    }

    public void setGrades(Grade grade) {
        this.grades = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void done() {
        done = true;
    }

    public void setDone(boolean bool) {
        done = bool;
    }

    // Checks if the session is done
    public boolean isDone() {
        return done;
    }

    /**
     * Checks if the student takes a specific subject.
     * @param subject The subject to check.
     * @return True if the student takes the subject, false otherwise.
     */
    public boolean takesSubject(Subject subject) {
        return grades.hasSubject(subject);
    }

    /**
     * Retrieves the mark for a specific subject.
     * @param subject The subject for which to get the mark.
     * @return The mark for the specified subject.
     */
    public int mark(Subject subject) {
        return grades.getMark(subject);
    }

    /**
     * Runs the student interface, displaying the student's frame.
     * This method is executed when the Student object is run in a thread.
     */
    @Override
    public void run() {
        StudFrame frame = new StudFrame(this, grades, name);
        frame.setVisible(true);
    }

    /*public static void main(String[] args) {
        new Student("Riri Momo").run();
    }*/
}