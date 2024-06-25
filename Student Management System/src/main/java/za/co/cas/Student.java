package za.co.cas;

import java.util.*;

public class Student implements Runnable{
    private final String name;
    private final String studentNumber;
    private String id;
    private final Grade grades;
    private Manager manager;
    private boolean done;

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
        done = false;
    }

    public Student(Manager manager, String fullName) {
        this(fullName);
        this.manager = manager;
    }

    public void done() {
        done = true;
    }

    @Override
    public void run() {
        StudFrame frame = new StudFrame(this, grades, name);
        frame.setVisible(true);
        while (!done) {
            try {
                //noinspection BusyWait
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
//                System.out.println(ignored.getMessage());
            }
        }
        Manager.showFrame(manager);
    }
    public static void main(String[] args) {
        new Student("Riri Momo").run();
    }
}