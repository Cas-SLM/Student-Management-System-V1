package za.co.cas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataManager extends Mapper{

    private final Manager manager;

    public DataManager(Manager manager) {
        super("/home/cas/Documents/CODSOFT/Student Management System/src/main/java/za/co/cas/Students.json");
        this.manager = manager;
    }

    public ArrayList<Student> read() {
        ArrayList<Student> studentList = new ArrayList<>();
        ArrayList<LinkedHashMap<String, ?>> jsonList;
        try {
            jsonList = mapper.readValue(Files.readString(Path.of(FILE.getAbsolutePath())), ArrayList.class);
            for (HashMap<String, ?> studentHashMap : jsonList) {
                HashMap<String, ?> newStudentMap = mapper.readValue(mapper.writeValueAsString(studentHashMap), HashMap.class);
                Student student = new Student(manager, (String) newStudentMap.get("name"));
                for (String key : newStudentMap.keySet()) {
                    switch (key) {
                        case "grades":
                            Grade grade = new Grade();
                            if (newStudentMap.get("grades") instanceof HashMap) {
                                HashMap<String, ?> gradesMap = (HashMap<String, Integer>) newStudentMap.get("grades");
                                if (gradesMap.get("subjectsAndMarks") instanceof HashMap) {
                                    grade.setSubjectsAndMarks((HashMap<String, Integer>) gradesMap.get("subjectsAndMarks"));
                                }
                            } else {
                                grade.setSubjectsAndMarks(new HashMap<>());
                            }
                            student.setGrades(grade);
                            break;
                        case "id":
                            if (newStudentMap.get("id") instanceof String)
                                student.setId((String) newStudentMap.get(key));
                            else
                                student.setId(null);
                            break;
                        case "done":
                            student.setDone((boolean) newStudentMap.get(key));
                            break;
                        default:
                            break;

                    }
                }
                studentList.add(student);
            }
        } catch (IOException e) {
            studentList = new ArrayList<>();
        }
        return studentList;
    }

    public void write(ArrayList<Student> students) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, students);
        } catch (IOException ignored) {
        }
    }
}
