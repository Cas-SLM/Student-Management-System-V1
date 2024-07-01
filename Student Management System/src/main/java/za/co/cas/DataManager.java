package za.co.cas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static za.co.cas.SP.sp;

/**
 * DataManager handles reading and writing student data from/to a JSON file.
 * It extends the Mapper class to utilize JSON parsing and file handling.
 */
public class DataManager extends Mapper {

    /**
     * Constructor that sets the file path for storing student data.
     */
    public DataManager() {
        super(System.getProperty("user.dir")+sp()+"Student Management System"+sp()+"src"+sp() +"main"+sp() +"java"+sp() +"za"+sp() +"co"+sp() +"cas"+sp()+ "Students.json");
        File file = new File(System.getProperty("user.dir")+sp()+"Student Management System"+sp()+"src"+sp() +"main"+sp() +"java"+sp() +"za"+sp() +"co"+sp() +"cas"+sp()+ "Students.json");
        if (!file.exists()) {
            try {
                var i = file.setWritable(true);
                i = file.createNewFile();
                Files.writeString(file.toPath(), "[]");
            } catch (IOException e) {
                System.out.printf("Cannot create file: %s%n", file.getName());
            }
        }


    }

    /**
     * Reads student data from a JSON file and returns a list of Student objects.
     *
     * @return An ArrayList of Student objects.
     */
    public ArrayList<Student> read() {
        ArrayList<Student> studentList = new ArrayList<>();
        List<LinkedHashMap<String, ?>> jsonList;
        try {
            // Read JSON data from the file and parse it into a list of LinkedHashMaps
            jsonList = mapper.readValue(Files.readString(Path.of(FILE.getAbsolutePath())), List.class);//mapper.getTypeFactory().constructCollectionType(List.class, ArrayList.class));

            // Iterate through the list of student data
            for (HashMap<String, ?> studentHashMap : jsonList) {
                // Convert each student's data to a HashMap
                HashMap<String, ?> newStudentMap = mapper.readValue(mapper.writeValueAsString(studentHashMap), HashMap.class);//mapper.getTypeFactory().constructCollectionType(List.class, HashMap.class));
                Student student = new Student((String) newStudentMap.get("name"));

                // Populate student object with data from the map
                for (String key : newStudentMap.keySet()) {
                    switch (key) {
                        case "grades":
                            Grade grade = new Grade();
                            if (newStudentMap.get("grades") instanceof HashMap) {
                                @SuppressWarnings("unchecked")
                                HashMap<String, ?> gradesMap = (HashMap<String, Integer>) newStudentMap.get("grades");
                                if (gradesMap.get("subjectsAndMarks") instanceof HashMap) {
                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Integer> map = (HashMap<String, Integer>) gradesMap.get("subjectsAndMarks");
                                    grade.setSubjectsAndMarks(map);
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
                studentList.add(student); // Add the student to the list
            }
        } catch (IOException e) {
            studentList = new ArrayList<>(); // Return an empty list on error
        }
        return studentList;
    }

    /**
     * Writes the list of students to a JSON file.
     *
     * @param students An ArrayList of Student objects to write to the file.
     */
    public void write(ArrayList<Student> students) {
        try {
            // Write the list of students to the JSON file with pretty printing
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, students);
        } catch (IOException ignored) {
            // Ignore exceptions during write operations
        }
    }
}

