package za.co.cas;

import java.util.*;

public class Student implements Runnable{
    private final String name;
//    private final String number;
    private final String id;
    private final Grade grades;
//    private final Mapper mapper;
    private Manager manager;
    private boolean done;

    public Student(String fullName) {
        Random random = new Random();
        this.name = fullName;
        this.id = null;
        /*ArrayList<String> student = new ArrayList<>(Arrays.asList(fullName.toLowerCase().strip().split(" ")));
        this.number = student.getFirst().toUpperCase().substring(0, 2) +
                String.format("%d", random.nextInt(111, 1000)) +
                student.getLast().toUpperCase().substring(0, 2) +
                String.format("%d", random.nextInt(111, 1000));*/
//        grades = new Grade(this.name);
        grades = new Grade();
        done = false;
    }

    public Student(Manager manager, String fullName) {
        this(fullName);
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public Grade getGrades() {
        return grades;
    }

    public String getId() {
        return id;
    }

    public void done() {
        done = true;
    }

    public boolean takesSubject(Subject subject) {
        return grades.hasSubject(subject);
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
            }
        }
        Manager.showFrame(manager);
    }

    public static void main(String[] args) {
        new Student("Riri Momo").run();
    }

    public int mark(Subject subject) {
        return grades.getMark(subject);
    }
}