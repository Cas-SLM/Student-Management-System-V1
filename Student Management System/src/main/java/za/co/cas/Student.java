package za.co.cas;

import java.util.*;

public class Student implements Runnable{
    private String name;
    private String id;
    private Grade grades;
    private Manager manager;
    private boolean done;

    public Student(String fullName) {
        this.name = fullName;
        this.id = null;
        /*ArrayList<String> student = new ArrayList<>(Arrays.asList(fullName.toLowerCase().strip().split(" ")));
        this.number = student.getFirst().toUpperCase().substring(0, 2) +
                String.format("%d", random.nextInt(111, 1000)) +
                student.getLast().toUpperCase().substring(0, 2) +
                String.format("%d", random.nextInt(111, 1000));
        grades = new Grade(this.name);*/
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

    public boolean isDone() {
        return done;
    }

    public boolean takesSubject(Subject subject) {
        return grades.hasSubject(subject);
    }

    @Override
    public void run() {
        StudFrame frame = new StudFrame(this, grades, name);
        frame.setVisible(true);
//        Manager.showFrame(manager);
    }

    public static void main(String[] args) {
        new Student("Riri Momo").run();
    }

    public int mark(Subject subject) {
        return grades.getMark(subject);
    }
}