package za.co.cas;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager extends JFrame {
    ArrayList<Student> Students;// = new ArrayList<>();
    DataManager dataManager = new DataManager();
    DefaultTableModel tblModel;
    public Manager() {
        setTitle("Student Manager");
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Students = dataManager.read();

        JTextField searchInput = new JTextField("", 30);

        JButton addStudent = new JButton("Add");

        JButton removeStudent = new JButton("Remove");
        JButton editStudent = new JButton("Edit");
        JButton searchStudent = new JButton("Search");

        JPanel searchPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 60);
            }
        };
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(new JLabel("Search"), BorderLayout.WEST);
        searchPanel.add(searchInput, BorderLayout.CENTER);
        searchPanel.add(searchStudent, BorderLayout.SOUTH);


        JPanel ctrlBtnPnl = new JPanel(new GridLayout(1, 3, 10, 0));
        ctrlBtnPnl.add(addStudent);
        ctrlBtnPnl.add(removeStudent);
        ctrlBtnPnl.add(editStudent);
        String[] column = {"Number", "Name", "Grade", "Average", "Subjects"};

        tblModel = new DefaultTableModel();

        JTable studentTable = new JTable(tblModel);
        tblModel.setColumnIdentifiers(column);

        addStudent.addActionListener(e -> {
            hideFrame(this);
            String name = JOptionPane.showInputDialog("Enter students name");
            Student student = new Student(this, name);
            Thread gradesWindow = new Thread(student);
            gradesWindow.start();
            Students.add(student);
            dataManager.write(Students);
            loadTableData(Students);
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.NORTH);
        add(searchPanel);
        add(ctrlBtnPnl, BorderLayout.SOUTH);

    }

    private void loadTableData(ArrayList<Student> students) {
        tblModel.setRowCount(0);
        int i = 1;
        for (Student student : students) {
            Object[] row = {i, student.getName(), student.getGrades().getGrade(), student.getGrades().getAverage(), student.getGrades().getSubjects()};
            tblModel.addRow(row);
            i++;
        }
    }

    private static void hideFrame(Manager mnger) {
        if (mnger != null)
            mnger.setVisible(false);
    }

    public static void showFrame(Manager mnger) {
        if (mnger != null)
            mnger.setVisible(true);
    }

    public static void main(String[] args) {
        Manager mng = new Manager();
        mng.setVisible(true);
    }

    class DataManager extends Mapper{

        public DataManager() {
            super("/home/cas/Documents/CODSOFT/Student Management System/src/main/java/za/co/cas/Students.json");
        }

        public ArrayList<Student> read() {
            ArrayList<Student> out;
            try {
                out = mapper.readValue(FILE, new TypeReference<ArrayList<Student>>() {});
            } catch (IOException e) {
                out = new ArrayList<>();
            }
            return out;
        }

        public void write(ArrayList<Student> students) {
            try {
                mapper.writeValue(FILE, students);
            } catch (IOException ignored) {
            }
        }
    }
}