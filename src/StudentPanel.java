import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This is the class that represents a Student tab on the GUI.
 * 
 * @author Xinyu Zhao
 * @version 6
 */
public class StudentPanel extends JPanel {
	private List<Student> allStudents;
	private DefaultListModel<String> studentListModel;
	private JList<String> studentList;

	private SQLiteManager sqlManager;
	private String dataDir;

	private List<Student> classStudents;
	private DefaultListModel<String> classStudentListModel;
	private JList<String> classStudentList;

	private List<Classroom> allClassrooms;
	private DefaultListModel<String> classroomListModel;
	private JList<String> classroomList;

	private static JButton addStudentButton = new JButton("Add Student");
	private static JButton editStudentButton = new JButton("Edit Student");
	private static JButton deleteStudentButton = new JButton("Delete Student");

	private static JButton addStudentToClassroom = new JButton("Add to Classroom");
	private static JButton removeStudentFromClassroom = new JButton("Remove from Classroom");

	private static JButton addClassroomButton = new JButton("Add Classroom");
	private static JButton editClassroomButton = new JButton("Edit Classroom");
	private static JButton deleteClassroomButton = new JButton("Delete Classroom");

	/**
	 * This constructor sets up the basic GUI for the panel.
	 * 
	 * @param dataDir    is the path to the resource folder
	 * @param sqlManager is the database
	 */
	public StudentPanel(String dataDir, SQLiteManager sqlManager) {
		this.dataDir = dataDir;
		this.sqlManager = sqlManager;

		setLayout(new GridBagLayout());

		addClassrooms();
		addClassStudents();
		addStudents();
	}

	/**
	 * This method can modify Classrooms based on what buttons are pressed and add a
	 * new classroom, edit a selected, or remove a classroom.
	 */
	public void addClassrooms() {
		allClassrooms = sqlManager.getAllClassrooms();

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 1));
		cmdPanel.add(addClassroomButton);
		cmdPanel.add(editClassroomButton);
		cmdPanel.add(deleteClassroomButton);

		classroomListModel = new DefaultListModel<String>();
		if (allClassrooms != null) {
			for (Classroom classroom : allClassrooms) {
				classroomListModel.addElement(classroom.getCourseName());
			}
		}
		classroomList = new JList<String>(classroomListModel);

		classroomList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int i = classroomList.getSelectedIndex();
					if (i < 0) {
						return;
					}
					Classroom classroom = allClassrooms.get(i);
					List<Integer> studentIds = sqlManager.getStudentsInClassroom(classroom.getId());
					classStudentListModel.clear();
					classStudents = new ArrayList<Student>();
					if (studentIds == null) {
						return;
					}
					for (Integer studentId : studentIds) {
						Student student = sqlManager.getStudent(studentId);
						classStudents.add(student);
						classStudentListModel.addElement(student.getName());
					}
				}
			}
		});

		addClassroomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Classroom classroom = getClassroomInfo(null);
				if (classroom != null) {
					sqlManager.addClassroom(classroom);
					allClassrooms.add(classroom);
					classroomListModel.addElement(classroom.getCourseName());
				}
			}
		});

		editClassroomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = classroomList.getSelectedIndex();
				if (i < 0) {
					return;
				}
				Classroom classroom = allClassrooms.get(i);
				Classroom newClassroom = getClassroomInfo(classroom);
				if (newClassroom != null) {
					sqlManager.updateClassroom(newClassroom);
					classroom.setTeacherName(newClassroom.getTeacherName());
				}
			}
		});

		deleteClassroomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = classroomList.getSelectedIndex();
				if (i < 0) {
					return;
				}
				Classroom classroom = allClassrooms.get(i);
				if (sqlManager.deleteClassroom(classroom.getId())) {
					classroomListModel.remove(i);
					allClassrooms.remove(i);
					classStudents.clear();
					classStudentListModel.clear();

					// delete all students from the classroom
					for (Student student : allStudents) {
						sqlManager.deleteStudentFromClassroom(student.getID(), classroom.getId());
					}
				}
			}
		});

		classroomList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		classroomList.setLayoutOrientation(JList.VERTICAL);
		classroomList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(classroomList);
		listScroller.setBorder(BorderFactory.createTitledBorder("Classrooms"));
		listScroller.setPreferredSize(new Dimension(200, 200));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(cmdPanel, gbc);

		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(listScroller, gbc);
	}

	/**
	 * This method adds or removes a student into or from a class based on what
	 * button is pressed and which class and student is selected.
	 */
	public void addClassStudents() {
		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(2, 1));
		cmdPanel.add(addStudentToClassroom);
		cmdPanel.add(removeStudentFromClassroom);

		classStudentListModel = new DefaultListModel<String>();
		if (classStudents != null) {
			for (Student student : classStudents) {
				classStudentListModel.addElement(student.getName());
			}
		}
		classStudentList = new JList<String>(classStudentListModel);

		addStudentToClassroom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int classIndex = classroomList.getSelectedIndex();
				if (classIndex < 0) {
					return;
				}

				Classroom classroom = allClassrooms.get(classIndex);

				int studentIndex = studentList.getSelectedIndex();
				if (studentIndex < 0) {
					return;
				}
				Student student = allStudents.get(studentIndex);

				if (sqlManager.addStudentToClassroom(student.getID(), classroom.getId())) {
					classStudents.add(student);
					classStudentListModel.addElement(student.getName());
				}

			}
		});

		removeStudentFromClassroom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int classIndex = classroomList.getSelectedIndex();
				if (classIndex < 0) {
					return;
				}

				Classroom classroom = allClassrooms.get(classIndex);

				int classStudentIndex = classStudentList.getSelectedIndex();
				if (classStudentIndex < 0) {
					return;
				}
				Student student = classStudents.get(classStudentIndex);

				if (sqlManager.deleteStudentFromClassroom(student.getID(), classroom.getId())) {
					classStudents.remove(classStudentIndex);
					classStudentListModel.remove(classStudentIndex);
				}
			}
		});

		classStudentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		classStudentList.setLayoutOrientation(JList.VERTICAL);
		classStudentList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(classStudentList);
		listScroller.setBorder(BorderFactory.createTitledBorder("Students in Classroom (left)"));
		listScroller.setPreferredSize(new Dimension(200, 200));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1.0;
		gbc.weightx = 1;
		gbc.gridx = 2;
		gbc.gridy = 0;
		add(listScroller, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 3;
		gbc.gridy = 0;

		add(cmdPanel, gbc);
	}

	/**
	 * This method modifies Students in a class based on what buttons are pressed
	 * and add a new student, edit a selected, or remove a student.
	 */
	public void addStudents() {

		allStudents = sqlManager.getAllStudents();

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 1));
		cmdPanel.add(addStudentButton);
		cmdPanel.add(editStudentButton);
		cmdPanel.add(deleteStudentButton);

		studentListModel = new DefaultListModel<String>();
		if (allStudents != null) {
			for (Student student : allStudents) {
				studentListModel.addElement(student.getName());
			}
		}
		studentList = new JList<String>(studentListModel);

		addStudentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Student student = getStudentInfo(null);
				if (student != null) {
					sqlManager.addStudent(student);
					studentListModel.addElement(student.getName());
					allStudents.add(student);
				}
			}
		});

		editStudentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = studentList.getSelectedIndex();
				if (i < 0) {
					return;
				}
				Student student = allStudents.get(i);
				Student newStudent = getStudentInfo(student);
				if (newStudent != null) {
					sqlManager.updateStudent(newStudent);
					student.setGrade(newStudent.getGrade());
				}
			}
		});

		deleteStudentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = studentList.getSelectedIndex();
				if (i < 0) {
					return;
				}
				Student student = allStudents.get(i);
				if (sqlManager.deleteStudent(student.getID())) {
					allStudents.remove(i);
					studentListModel.remove(i);
				}

				// delete student from all classrooms
				for (Classroom classroom : allClassrooms) {
					sqlManager.deleteStudentFromClassroom(student.getID(), classroom.getId());
				}
				if (classStudents != null) {
					// delete student from the GUI list and model
					for (int k = 0; k < classStudents.size(); ++k) {
						if (classStudents.get(k).getID() == student.getID()) {
							classStudents.remove(k);
							classStudentListModel.remove(k);
							break;
						}
					}
				}
			}
		});

		studentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		studentList.setLayoutOrientation(JList.VERTICAL);
		studentList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(studentList);
		listScroller.setBorder(BorderFactory.createTitledBorder("All Students"));
		listScroller.setPreferredSize(new Dimension(200, 200));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1.0;
		gbc.weightx = 1;
		gbc.gridx = 4;
		gbc.gridy = 0;
		add(listScroller, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 5;
		gbc.gridy = 0;

		add(cmdPanel, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;

		JButton helpButton = new JButton("Help");
		helpButton.setBackground(Color.GREEN);
		helpButton.setOpaque(true);
		add(helpButton, gbc);

		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHelp();
			}
		});
	}

	/**
	 * This method adds a newly inputed student to the system.
	 * 
	 * @param student that is too be added
	 * @return new student
	 */
	private Student getStudentInfo(Student student) {
		JTextField idText = new JTextField();
		JTextField nameText = new JTextField();
		JTextField gradeText = new JTextField();

		if (student != null) {
			idText.setText(student.getID() + "");
			idText.setEnabled(false);

			nameText.setText(student.getName());
			nameText.setEnabled(false);

			gradeText.setText(student.getGrade() + "");
		}

		final JComponent[] inputs = new JComponent[] { new JLabel("ID"), idText, new JLabel("Name"), nameText,
				new JLabel("Grade"), gradeText };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Student Info", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int id = Integer.parseInt(idText.getText().trim());
				String name = nameText.getText().trim();
				int grade = Integer.parseInt(gradeText.getText().trim());
				if (student == null) {
					student = new Student(id, name);
				}
				student.setGrade(grade);

				return student;
			} catch (Exception e) {
			}
		}

		return null;

	}

	/**
	 * This method adds a new classroom based on the user input.
	 * 
	 * @param classroom that is too be added
	 * @return new classroom
	 */
	private Classroom getClassroomInfo(Classroom classroom) {
		JTextField idText = new JTextField();
		JTextField courseNameText = new JTextField();
		JTextField teacherNameText = new JTextField();

		if (classroom != null) {
			idText.setText(classroom.getId() + "");
			idText.setEnabled(false);

			courseNameText.setText(classroom.getCourseName());
			courseNameText.setEnabled(false);
			teacherNameText.setText(classroom.getTeacherName());
		}

		final JComponent[] inputs = new JComponent[] { new JLabel("Id"), idText, new JLabel("Course"), courseNameText,
				new JLabel("Teacher"), teacherNameText };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Classroom Info", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int id = Integer.parseInt(idText.getText().trim());
				String courseName = courseNameText.getText().trim();
				String teacherName = teacherNameText.getText().trim();
				if (classroom == null) {
					classroom = new Classroom(id, courseName);
				}
				classroom.setTeacherName(teacherName);

				return classroom;
			} catch (Exception e) {
			}
		}

		return null;

	}

	/**
	 * This method represents the text shown when the help button is clicked to help
	 * the user maneuver the StudentPanel.
	 */
	private void showHelp() {
		String msg = "<HTML><BODY>Help Message</BODY></HTML>";
		JOptionPane.showMessageDialog(this, msg, "Attendence Help", JOptionPane.PLAIN_MESSAGE);
	}

}
