package za.co.cas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Manager extends JFrame {
    ArrayList<Student> Students = new ArrayList<>();
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
        String[] column = {"Number", "Name", "Grade", "Average"};
        ArrayList<ArrayList<String>> dataArr = new ArrayList<>() {{
            add(new ArrayList<>(Arrays.asList("0","Riri Momo", "4.5", "90%")));
            add(new ArrayList<>(Arrays.asList("1","Siri Nomo", "4.0", "80%")));
        }};

        JTable studentTable = new JTable(getData(dataArr), column);

        addStudent.addActionListener(e -> {
            hideFrame(this);
            String name = JOptionPane.showInputDialog("Enter students name");
            Student student = new Student(this, name);
            Thread gradesWindow = new Thread(student);
            gradesWindow.start();
            Students.add(student);
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.NORTH);
        add(searchPanel);
        add(ctrlBtnPnl, BorderLayout.SOUTH);

    }

    private static String[][] getData(ArrayList<ArrayList<String>> dataArr) {
        String[][] data = new String[dataArr.size()][];
        int i = 0;
        for (ArrayList<String> list : dataArr) {
            data[i++] = list.toArray(new String[0]);
        }
        return data;
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
}