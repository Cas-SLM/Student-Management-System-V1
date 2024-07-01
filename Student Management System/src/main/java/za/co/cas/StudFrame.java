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

/**
 * Represents a JFrame for managing student details and grades.
 */
class StudFrame extends JFrame {
    private Student student; // The student associated with this frame
    private final HashMap<Subject, JCheckBox> subjectCheckBox; // Maps subjects to their checkboxes
    private final HashMap<Subject, JSpinner> subjectSpinner; // Maps subjects to their spinners

    /**
     * Constructs a new StudFrame for a student.
     * @param grades The grades of the student.
     * @param name The name of the student.
     */
    public StudFrame(Grade grades, String name) {
        setTitle("Student - %s".formatted(name));
        setSize(1100, 320);
        setMinimumSize(new Dimension(1100, 320));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize subject maps with empty placeholders
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

        // Panel for displaying subjects and their associated checkboxes and spinners
        JPanel subjects = new JPanel() {{
            setLayout(new GridLayout(11, 3, 0, 0)); // 11 rows, 3 columns grid layout
            ArrayList<Subject> subjectList = new ArrayList<>(subjectCheckBox.keySet());
            subjectList.sort(Comparator.comparing(Enum::name)); // Sort subjects alphabetically
            for (Subject key : subjectList) {
                JPanel subject = new JPanel() {{
                    setLayout(new BorderLayout());
                    JCheckBox checkbox = getCheckBox(key, grades);
                    JSpinner spinner = getSpinner(key, grades);
                    subjectCheckBox.put(key, checkbox); // Store checkbox in the map
                    subjectSpinner.put(key, spinner); // Store spinner in the map
                    preset(key, spinner, checkbox); // Pre-set values if student already takes this subject
                    add(subjectCheckBox.get(key), BorderLayout.WEST);
                    add(subjectSpinner.get(key), BorderLayout.EAST);
                }};
                add(subject); // Add subject panel to subjects panel
            }
        }};

        // Panel for 'Done' button
        JPanel doneBtn = new JPanel();
        JButton done = getDoneButton(this, grades); // Get 'Done' button for current frame
        doneBtn.add(done);

        // Layout setup for the frame
        setLayout(new BorderLayout());
        add(subjects, BorderLayout.NORTH); // Add subjects panel to the top
        add(doneBtn, BorderLayout.SOUTH); // Add 'Done' button panel to the bottom

        // Window closing event listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                student.setDone(true); // Set student as done when window closes
                e.getWindow().dispose(); // Dispose the window
            }
        });

    }

    /**
     * Constructs a new StudFrame for an existing student.
     * @param student The student object.
     * @param grades The grades of the student.
     * @param name The name of the student.
     */
    public StudFrame(Student student, Grade grades, String name) {
        this(grades, name); // Invoke the constructor with grades and name
        this.student = student; // Set the student associated with this frame
    }

    /**
     * Creates and returns the 'Done' button.
     * @param frame The current StudFrame.
     * @param grades The grades of the student.
     * @return The 'Done' button.
     */
    private JButton getDoneButton(StudFrame frame, Grade grades) {
        JButton done = new JButton("Done");

        // ActionListener for 'Done' button
        done.addActionListener(e -> {
            for (Subject key : subjectCheckBox.keySet()) {
                JSpinner spn = subjectSpinner.get(key); // Get spinner for current subject
                JCheckBox chk = subjectCheckBox.get(key); // Get checkbox for current subject
                if (chk.isSelected()) {
                    grades.addSubject(key, (int) spn.getValue()); // Add subject and mark to grades
                }
            }
            frame.dispose(); // Dispose the frame
            student.done(); // Mark student as done
        });

        done.setSize(50, 30); // Set button size (not necessary if using layout managers)
        return done;
    }

    /**
     * Sets initial values for spinner and checkbox if the student already takes the subject.
     * @param key The subject.
     * @param spinner The spinner associated with the subject.
     * @param checkBox The checkbox associated with the subject.
     */
    private void preset(Subject key, JSpinner spinner, JCheckBox checkBox) {
        // If student exists and takes the subject, set spinner value and checkbox as selected
        if (student != null && student.takesSubject(key)) {
            spinner.setValue(student.mark(key));
            checkBox.setSelected(true);
        }
    }

    /**
     * Creates and returns a spinner for a subject.
     * @param subject The subject.
     * @param grades The grades of the student.
     * @return The spinner for the subject.
     */
    private JSpinner getSpinner(Subject subject, Grade grades) {
        SpinnerModel values = new SpinnerNumberModel(50, 0, 100, 1); // Spinner values (default)
        JSpinner spinner = new JSpinner(values); // Create spinner with default values
        spinner.setValue(grades.getMark(subject));

        // Set up formatter for spinner
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0);
        formatter.setMaximum(100);

        // Set editor and text field for spinner
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
        JFormattedTextField textField = editor.getTextField();
        textField.addActionListener(e -> {
            if (subjectSpinner.get(subject) != null) {
                grades.addMark(subject, (int) subjectSpinner.get(subject).getValue()); // Add mark to grades
            }
        });

        textField.setFormatterFactory(new DefaultFormatterFactory(formatter)); // Set formatter factory
        spinner.setEditor(editor); // Set editor for spinner

        // Change listener for spinner
        spinner.addChangeListener(e -> {
            grades.addMark(subject, (int) ((JSpinner) e.getSource()).getValue()); // Add mark to grades
        });

        return spinner;
    }

    /**
     * Creates and returns a checkbox for a subject.
     * @param key The subject.
     * @param grades The grades of the student.
     * @return The checkbox for the subject.
     */
    private JCheckBox getCheckBox(Subject key, Grade grades) {
        boolean checked = false;
        if (grades != null && grades.getSubjectsAndMarks().containsKey(key)) {
            checked = true; // Check if subject exists in grades
        }
        JCheckBox checkbox = new JCheckBox(key.name(), checked); // Create checkbox with subject name and checked status
        checkbox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                grades.addSubject(key, (int) subjectSpinner.get(key).getValue()); // Add subject to grades
            } else {
                grades.removeSubject(key); // Remove subject from grades
            }
        });
        return checkbox;
    }
}
