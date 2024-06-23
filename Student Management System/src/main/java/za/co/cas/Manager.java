package za.co.cas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class Manager extends JFrame {
    ArrayList<Student> Students = new ArrayList<>(){{
        Student student = new Student("Riri Momo");
        student.run();
        add(student);

    }};
    public Manager() {
        setTitle("Student Manager");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /*JTextField nameInput = new JTextField("", 30);*/
        JTextField searchInput = new JTextField("", 30);
        JButton addStudent = new JButton("Add");
        addStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO Run student addition fame and hide this one
            }
        });
        JButton removeStudent = new JButton("Remove");
        JButton editStudent = new JButton("Edit");
        JButton searchStudent = new JButton("Search");

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setSize(500, 50);
        searchPanel.add(new JLabel("Search"), BorderLayout.WEST);
        searchPanel.add(searchInput, BorderLayout.EAST);
        searchPanel.add(searchStudent, BorderLayout.SOUTH);


        JPanel ctrlBtnPnl = new JPanel(new GridLayout(1, 3, 10, 0));
        ctrlBtnPnl.add(addStudent);
        ctrlBtnPnl.add(removeStudent);
        ctrlBtnPnl.add(editStudent);
        String[] column = {"Number", "Name", "Grade", "Average"};
        ArrayList<ArrayList<String>> dataArr = new ArrayList<>() {{
            add(new ArrayList<>(Arrays.asList("1234","Riri Momo", "4.5", "90%")));
            add(new ArrayList<>(Arrays.asList("1235","Siri Nomo", "4.0", "80%")));
        }};

        JTable studentTable = new JTable(getData(dataArr), column);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(studentTable));
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

    public static void main(String[] args) {
        Manager mng = new Manager();
        mng.setVisible(true);
    }
}