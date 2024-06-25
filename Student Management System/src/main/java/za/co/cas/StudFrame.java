package za.co.cas;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import java.util.HashMap;

class StudFrame extends JFrame {
    private Student student;
    private final HashMap<Subject, JCheckBox> subjectCheckBox;
    private final HashMap<Subject, JSpinner> subjectSpinner;

    public StudFrame(Grade grades, String name) {
        setTitle("Student - %s".formatted(name));
        setSize(550, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            setLayout(new GridLayout(9, 2, 0, 0));
            for (Subject key : subjectCheckBox.keySet()) {
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
        JButton done = getjButton(this, grades);
        doneBtn.add(done);

        setLayout(new BorderLayout());
        add(subjects, BorderLayout.NORTH);
        add(doneBtn, BorderLayout.SOUTH);

    }

    public StudFrame(Student student, Grade grades, String name) {
        this(grades, name);
        this.student = student;
    }

    private JButton getjButton(StudFrame frame, Grade grades) {
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
        });
        done.setSize(50, 30);
        return done;
    }

    private void preset(Subject key, JSpinner spinner, JCheckBox checkBox) {
        if (student != null && student.takesSubject(key)) {
            spinner.setValue(student.getMark(key));
            checkBox.setSelected(true);
        }

    }

    private JSpinner getSpinner(Subject subject, Grade grades) {
        SpinnerModel values = new SpinnerNumberModel(50, 0, 100, 1);
        JSpinner spinner = new JSpinner(values);

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
                grades.addMark(subject, (int) subjectSpinner.get(subject).getValue());
                System.out.println(grades.tabulate());
            }
        });

        textField.setFormatterFactory(new DefaultFormatterFactory(formatter));
        spinner.setEditor(editor);

        spinner.addChangeListener(e -> {
            grades.addMark(subject, (int) ((JSpinner) e.getSource()).getValue());
            System.out.println(grades.tabulate());
        });
        return spinner;
    }

    private JCheckBox getCheckBox(Subject key, Grade grades) {
        JCheckBox checkbox = new JCheckBox(key.name(), false);
        checkbox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                grades.addSubject(key, (int) subjectSpinner.get(key).getValue());
            } else {
                grades.removeSubject(key);
            }
            System.out.println(grades.tabulate());
        });
        return checkbox;
    }
}