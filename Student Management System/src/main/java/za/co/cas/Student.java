package za.co.cas;

import java.util.*;
import za.co.cas.Subject;
import za.co.cas.Subject.*;

public class Student {
    private final String name;
    private final String number;
    private final Grade grades;
    /*private double final_grade;
    private ArrayList<String> subjects;
    private Map<Subject, Double> marks;*/

    public Student(String fullName) {
        Random random = new Random();
        this.name = fullName;
        ArrayList<String> student = new ArrayList<>(Arrays.asList(fullName.toLowerCase().strip().split(" ")));
        StringBuilder number = new StringBuilder();
        number.append(student.getFirst().toUpperCase(), 0, 2)
                .append(String.format("%d", random.nextInt(111, 1000)))
                .append(student.getLast().toUpperCase(), 0, 2)
                .append(String.format("%d", random.nextInt(111, 1000)));
        this.number = number.toString();
        grades = new Grade();
        grades.randomSubjects(7);

    }

    public static void main(String[] args) {
    }

}