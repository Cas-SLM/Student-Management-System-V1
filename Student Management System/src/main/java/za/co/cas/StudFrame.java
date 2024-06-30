package za.co.cas;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

class StudFrame extends JFrame {
    private Student student;
    private final HashMap<Subject, JCheckBox> subjectCheckBox;
    private final HashMap<Subject, JSpinner> subjectSpinner;

    public StudFrame(Grade grades, String name) {
        setTitle("Student - %s".formatted(name));
        setSize(1100, 320);
        setMinimumSize(new Dimension(1100, 320));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        subjectCheckBox = new HashMap<>() {{
            for (Subject sub : Subject.values()) {
                put(sub, null);
            }
        }};
        subjectSpinner = new HashMap<>() {{
            for (Subject sub : Subject.values()) {
                put(sub, null);
            }
        }};

        JPanel subjects = new JPanel() {{
            setLayout(new GridLayout(11, 3, 0, 0));
            ArrayList<Subject> subjectList = new ArrayList<>(subjectCheckBox.keySet());
            subjectList.sort(Comparator.comparing(Enum::name));
            for (Subject key : subjectList) {
                JPanel subject = new JPanel() {{
                    setLayout(new BorderLayout());
                    setSize(200, 10);
                    JCheckBox checkbox = getCheckBox(key, grades);
                    JSpinner spinner = getSpinner(key, grades);
                    subjectCheckBox.put(key, checkbox);
                    subjectSpinner.put(key, spinner);
                    preset(key, spinner, checkbox);
                    add(subjectCheckBox.get(key), BorderLayout.WEST);
                    add(subjectSpinner.get(key), BorderLayout.EAST);
                }};
                add(subject);
            }
        }};

        JPanel doneBtn = new JPanel();
        JButton done = getDoneButton(this, grades);
        doneBtn.add(done);

        setLayout(new BorderLayout());
        add(subjects, BorderLayout.NORTH);
        add(doneBtn, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                student.setDone(true);
                e.getWindow().dispose();
            }
        });

    }

    public StudFrame(Student student, Grade grades, String name) {
        this(grades, name);
        this.student = student;
    }

    private JButton getDoneButton(StudFrame frame, Grade grades) {
        JButton done = new JButton("Done");

        done.addActionListener(e -> {
            for (Subject key : subjectCheckBox.keySet()) {
                JSpinner spn = subjectSpinner.get(key);
                JCheckBox chk = subjectCheckBox.get(key);
                if (chk.isSelected()) {
                    grades.addSubject(key, (int) spn.getValue());
                }
            }
            frame.dispose();
            student.done();
//            System.out.printf("Final Size: %s%n", this.getSize());
        });
        done.setSize(50, 30);
//        System.out.println(this.getSize());
        return done;
    }

    private void preset(Subject key, JSpinner spinner, JCheckBox checkBox) {
        if (student != null && student.takesSubject(key)) {
            spinner.setValue(student.mark(key));
            checkBox.setSelected(true);
        }

    }

    private JSpinner getSpinner(Subject subject, Grade grades) {
        SpinnerModel values = new SpinnerNumberModel(50, 0, 100, 1);
        JSpinner spinner = new JSpinner(values);
        if (grades != null) {
            spinner.setValue(grades.getMark(subject));
        }

        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0);
        formatter.setMaximum(100);

        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
        JFormattedTextField textField = editor.getTextField();
        textField.addActionListener(e -> {
            if (subjectSpinner.get(subject) != null) {
                assert grades != null;
                grades.addMark(subject, (int) subjectSpinner.get(subject).getValue());
            }
        });

        textField.setFormatterFactory(new DefaultFormatterFactory(formatter));
        spinner.setEditor(editor);

        spinner.addChangeListener(e -> {
            assert grades != null;
            grades.addMark(subject, (int) ((JSpinner) e.getSource()).getValue());
        });
        return spinner;
    }

    private JCheckBox getCheckBox(Subject key, Grade grades) {
        boolean checked = false;
        if (grades !=null)
            if (grades.getSubjectsAndMarks().containsKey(key)) {
                checked = true;
            }
        JCheckBox checkbox = new JCheckBox(key.name(), checked);
        checkbox.addItemListener(e -> {
            assert grades != null;
            if (e.getStateChange() == ItemEvent.SELECTED) {
                grades.addSubject(key, (int) subjectSpinner.get(key).getValue());
            } else {
                grades.removeSubject(key);
            }
        });
        return checkbox;
    }
}