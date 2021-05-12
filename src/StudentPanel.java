import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class StudentPanel extends JPanel {
	List<Student> allStudents;
	DefaultListModel<String> listModel;
	JList<String> studentList;
	private SQLiteManager sqlManager;
	private String dataDir;

	GridBagConstraints constraints = new GridBagConstraints();

	static JButton addButton = new JButton("Add Student");
	static JButton editButton = new JButton("Edit Student");
	static JButton deleteButton = new JButton("Delete Student");

	public StudentPanel(String dataDir, SQLiteManager sqlManager) {
		this.dataDir = dataDir;
		this.sqlManager = sqlManager;
		
		allStudents = sqlManager.getAllStudents();

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 1));
		cmdPanel.add(addButton);
		cmdPanel.add(editButton);
		cmdPanel.add(deleteButton);

		listModel = new DefaultListModel<String>();
		if (allStudents != null) {
			for(Student student : allStudents) {
				listModel.addElement(student.getName());
			}
		}
		studentList = new JList<String>(listModel);
		
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Student student = getStudentInfo(null);
				if (student != null) {
					sqlManager.addStudent(student);
					listModel.addElement(student.getName());
				}
			}
		});

		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = studentList.getSelectedIndex();
				if (i < 0 ) {
					return;
				}
				String studentName = listModel.get(i);
				Student student = sqlManager.getStudent(studentName);
				Student newStudent = getStudentInfo(student);
				if (newStudent != null) {
					sqlManager.updateStudent(newStudent);
				}
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = studentList.getSelectedIndex();
				if (i < 0 ) {
					return;
				}
				String studentName = listModel.get(i);
				listModel.remove(i);
				sqlManager.deleteStudent(studentName);
			}
		});

		studentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		studentList.setLayoutOrientation(JList.VERTICAL);
		studentList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(studentList);
		listScroller.setPreferredSize(new Dimension(200, 200));

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1.0;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(listScroller, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 1;
		gbc.gridy = 0;

		add(cmdPanel, gbc);
	}

	private Student getStudentInfo(Student student) {
		JTextField idText = new JTextField();
		JTextField nameText = new JTextField();
		JTextField gradeText = new JTextField();
		JTextField cityText = new JTextField();

		if (student != null) {
			idText.setText(student.getID() + "");
			idText.setEnabled(false);

			nameText.setText(student.getName());
			nameText.setEnabled(false);

			gradeText.setText(student.getGrade() + "");
			cityText.setText(student.getCity());
		}

		final JComponent[] inputs = new JComponent[] { new JLabel("ID"), idText, new JLabel("Name"), nameText,
				new JLabel("Grade"), gradeText, new JLabel("City"), cityText };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Student Info",
				  JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int id = Integer.parseInt(idText.getText().trim());
				String name = nameText.getText().trim();
				String grade = gradeText.getText().trim();
				String city = cityText.getText().trim();
				if (student == null) {
					student = new Student(id, name);
				} else {
					student.setGrade(grade);
					student.setCity(city);
				}

				return student;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;

	}
}
