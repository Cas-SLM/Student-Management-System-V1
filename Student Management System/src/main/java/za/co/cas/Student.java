package za.co.cas;

import java.util.*;

public class Student implements Runnable{
    private final String name;
    private final String studentNumber;
    private String id;
    private final Grade grades;

    public Student(String fullName) {
        Random random = new Random();
        this.name = fullName;
        this.id = "";
        ArrayList<String> student = new ArrayList<>(Arrays.asList(fullName.toLowerCase().strip().split(" ")));
        this.studentNumber = student.getFirst().toUpperCase().substring(0, 2) +
                String.format("%d", random.nextInt(111, 1000)) +
                student.getLast().toUpperCase().substring(0, 2) +
                String.format("%d", random.nextInt(111, 1000));
        grades = new Grade(this.name);
    }

    @Override
    public void run() {
        StudFrame frame = new StudFrame(grades);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        new Student("Riri Momo").run();
    }
}