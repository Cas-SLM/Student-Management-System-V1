package za.co.cas;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

public class Manager extends JFrame {
    ArrayList<Student> Students;
    DataManager dataManager;
    DefaultTableModel tblModel;
    DefaultTableCellRenderer cellRenderer;
    public Manager() {
        setTitle("Student Manager");
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer. setVerticalAlignment(SwingConstants.TOP);


        JTable studentTable = new JTable(tblModel);
        studentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        studentTable.setDefaultRenderer(Object.class, cellRenderer);
        tblModel.setColumnIdentifiers(column);

        studentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(60);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(1).setMaxWidth(500);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(2).setMaxWidth(60);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(3).setMaxWidth(80);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(250);

        addStudent.addActionListener(e -> {
            hideFrame(this);
            String name = JOptionPane.showInputDialog("Enter students name");
            Student student = new Student(this, name);
            Thread gradesWindow = new Thread(student);
            gradesWindow.start();
            Thread windowVisibility = new Thread( new Runnable() {
                @Override
                public void run() {
                    while (!student.isDone()) {
                        try {
                            //noinspection BusyWait
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    Students.add(student);
                    dataManager.write(Students);
                    loadTableData(Students);
                    showFrame(Manager.this);
                }
            });
            windowVisibility.start();
        });
        removeStudent.addActionListener(e -> {
            Student student;
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                int studentNumber = ((int) studentTable.getValueAt(selectedRow, 0)) - 1;
                student = Students.get(studentNumber);
                int edit = JOptionPane.showConfirmDialog(null, "Are you sure you want to Delete %s's information?".formatted(student.getName()));
                if (edit == 0) {
                    Students.remove(student);
                    dataManager.write(Students);
                    loadTableData(Students);
                }
            }
        });
        editStudent.addActionListener( e -> {
            Student student;
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                int studentNumber = ((int) studentTable.getValueAt(selectedRow, 0)) - 1;
                student = Students.get(studentNumber);
                int edit = JOptionPane.showConfirmDialog(null, "Are you sure you want to edit %s's grades and info?".formatted(student.getName()));
                if (edit == 0) {
                    student.setDone(false);
                    hideFrame(this);
                    Thread gradesWindow = new Thread(student);
                    gradesWindow.start();
                    Thread windowVisibility = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!student.isDone()) {
                                try {
                                    //noinspection BusyWait
                                    Thread.sleep(500);
                                } catch (InterruptedException ignored) {
                                }
                            }
                            System.out.println("Closing StudFrame, Opening Manager");
                            student.setDone(true);
                            dataManager.write(Students);
                            loadTableData(Students);
                            showFrame(Manager.this);
                        }
                    });
                    windowVisibility.start();
                }
            }
        });

        searchInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchStudent();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchStudent();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchStudent();
            }

            private void searchStudent() {
                if (searchInput.getText().isBlank()) {
                    loadTableData(Students);
                /*} else if (searchInput.getText().matches("(\\w+)?([^\\d])")) {
                    convertButton.setEnabled(false);*/
                } else {
                    ArrayList<Student> foundStudents = new ArrayList<>();
                    for (Student student : Students) {
                        if (student.getName().toLowerCase().contains(searchInput.getText().toLowerCase())) {
                            foundStudents.add(student);
                        }// else if ()
                    }
                    loadTableData(foundStudents);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        setLayout(new BorderLayout());

        JPanel bottomPnl = new JPanel();
        bottomPnl.add(searchPanel);
        bottomPnl.add(ctrlBtnPnl);
        bottomPnl.setLayout(new BoxLayout(bottomPnl, BoxLayout.PAGE_AXIS));

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPnl, BorderLayout.SOUTH);


        dataManager = new DataManager(this);
        Students = getData();
        loadTableData(Students);
    }

    private void loadTableData(ArrayList<Student> students) {
        tblModel.setRowCount(0);
        int i = 1;
        for (Student student : students) {
            StringBuilder subjects = new StringBuilder();
            int j = 1;
            ArrayList<Subject> subjectsList = student.getGrades().Subjects();
            for (Subject subject : subjectsList) {
                subjects.append(subject.toString());
                if (j!=subjectsList.size())
                    subjects.append(",\n");
                j++;
            }

            Object[] row = {i, student.getName(),
                    Double.valueOf("%.2f".formatted(student.getGrades().getGrade())),
                    Double.valueOf("%.2f".formatted(student.getGrades().getAverage())),
                    subjects.toString()};
            tblModel.addRow(row);
            i++;
        }
    }

    private ArrayList<Student> getData() {
        return dataManager.read();
    }

    private static void hideFrame(Manager manager) {
        if (manager != null)
            manager.setVisible(false);
    }

    public static void showFrame(Manager manager) {
        if (manager != null)
            manager.setVisible(true);
    }

    private void confirmDialog (String message, JTable studentTable, Consumer<String> function) {
        Student student;
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            int studentNumber = ((int) studentTable.getValueAt(selectedRow, 0)) - 1;
            student = Students.get(studentNumber);
            int edit = JOptionPane.showConfirmDialog(null, "Are you sure you want to edit %s's grades and info?".formatted(student.getName()));
            if (edit == 0) {
            }
        }
    }

    public static void main(String[] args) {
        Manager mng = new Manager();
        mng.setVisible(true);
    }
}