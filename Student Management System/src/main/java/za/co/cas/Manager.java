package za.co.cas;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * The Manager class represents a GUI application for managing student information.
 * It extends JFrame and provides functionalities to add, remove, edit, and search for students.
 */
public class Manager extends JFrame {
    ArrayList<Student> Students;// List to store student objects
    DataManager dataManager;// Manages data storage and retrieval
    DefaultTableModel tblModel;// Table model for displaying student data

    /**
     * Constructs a Manager object and initializes the GUI components.
     */
    public Manager() {
        setTitle("Student Manager");
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField searchInput = new JTextField("", 30); // Search input field

        // Buttons for various actions
        JButton addStudent = new JButton("Add");
        JButton removeStudent = new JButton("Remove");
        JButton editStudent = new JButton("Edit");
        JButton searchStudent = new JButton("Search");

        // Panel for search functionality
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

        // Panel for control buttons
        JPanel ctrlBtnPnl = new JPanel(new GridLayout(1, 3, 10, 0));
        ctrlBtnPnl.add(addStudent);
        ctrlBtnPnl.add(removeStudent);
        ctrlBtnPnl.add(editStudent);

        // Initialize table model and cell renderer
        String[] column = {"Number", "Name", "Grade", "Average", "Subjects"};
        tblModel = new DefaultTableModel();
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer(); // Cell renderer for table customization
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer. setVerticalAlignment(SwingConstants.TOP);

        // Table to display student data
        JTable studentTable = new JTable(tblModel);
        studentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        studentTable.setDefaultRenderer(Object.class, cellRenderer);
        tblModel.setColumnIdentifiers(column);

        // Set column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(60);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(1).setMaxWidth(500);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(2).setMaxWidth(60);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(3).setMaxWidth(80);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(250);

        // Action listener for adding a student
        addStudent.addActionListener(e -> {
            hideFrame(this);
            String name = JOptionPane.showInputDialog("Enter students name");
            Student student = new Student(name);
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
        // Action listener for removing a student
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
        // Action listener for editing a student
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
        // Document listener for search input
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

            /**
            *Method to search for student names based on the search input
            */
            private void searchStudent() {
                if (searchInput.getText().isBlank()) {
                    loadTableData(Students);
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

        // Scroll pane to contain the student table
        JScrollPane scrollPane = new JScrollPane(studentTable);
        setLayout(new BorderLayout());

        // Panel to contain the search and control buttons
        JPanel bottomPnl = new JPanel();
        bottomPnl.add(searchPanel);
        bottomPnl.add(ctrlBtnPnl);
        bottomPnl.setLayout(new BoxLayout(bottomPnl, BoxLayout.PAGE_AXIS));

        // Add components to the JFrame
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPnl, BorderLayout.SOUTH);

        // Initialize data manager and load student data
        dataManager = new DataManager();
        Students = getData();
        loadTableData(Students);
    }

    /**
     * Loads student data into the table model.
     * @param students List of students to be displayed in the table.
     */
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

            // Add student data to the table row
            Object[] row = {i, student.getName(),
                    Double.valueOf("%.2f".formatted(student.getGrades().getGrade())),
                    Double.valueOf("%.2f".formatted(student.getGrades().getAverage())),
                    subjects.toString()};
            tblModel.addRow(row);
            i++;
        }
    }

    /**
     * Retrieves student data from the data manager.
     * @return List of students.
     */
    private ArrayList<Student> getData() {
        return dataManager.read();
    }

    /**
     * Hides the Manager JFrame.
     * @param manager The Manager instance to hide.
     */
    private static void hideFrame(Manager manager) {
        if (manager != null)
            manager.setVisible(false);
    }

    /**
     * Shows the Manager JFrame.
     * @param manager The Manager instance to show.
     */
    public static void showFrame(Manager manager) {
        if (manager != null)
            manager.setVisible(true);
    }

    /**
     * Confirms an action through a dialog box.
     * @param message The confirmation message.
     * @param studentTable The table displaying student data.
     * @param function The function to execute upon confirmation.
     */
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
}