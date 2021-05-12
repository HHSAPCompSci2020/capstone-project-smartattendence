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
	DefaultListModel<String> studentListModel;
	JList<String> studentList;
	
	private SQLiteManager sqlManager;
	private String dataDir;

	List<Student> classStudents;
	DefaultListModel<String> classStudentListModel;
	JList<String> classStudentList;

	List<Classroom> allClassrooms;
	DefaultListModel<String> classroomListModel;
	JList<String> classroomList;

	GridBagConstraints constraints = new GridBagConstraints();

	static JButton addStudentButton = new JButton("Add Student");
	static JButton editStudentButton = new JButton("Edit Student");
	static JButton deleteStudentButton = new JButton("Delete Student");

	static JButton addStudentToClassroom = new JButton("<--Add--");
	static JButton removeStudentFromClassroom = new JButton("--Remove-->");

	static JButton addClassroomButton = new JButton("Add Classroom");
	static JButton editClassroomButton = new JButton("Edit Classroom");
	static JButton deleteClassroomButton = new JButton("Delete Classroom");

	public StudentPanel(String dataDir, SQLiteManager sqlManager) {
		this.dataDir = dataDir;
		this.sqlManager = sqlManager;
		
		setLayout(new GridBagLayout());

		addClassrooms();
		addClassStudents();
		addStudents();
	}
	
	void addClassrooms() {
		allClassrooms = sqlManager.getAllClassrooms();

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 1));
		cmdPanel.add(addClassroomButton);
		cmdPanel.add(editClassroomButton);
		cmdPanel.add(deleteClassroomButton);

		classroomListModel = new DefaultListModel<String>();
		if (allClassrooms != null) {
			for(Classroom classroom : allClassrooms) {
				classroomListModel.addElement(classroom.getName());
			}
		}
		classroomList = new JList<String>(classroomListModel);
		
		addClassroomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Classroom classroom = getClassroomInfo(null);
				if (classroom != null) {
					sqlManager.addClassroom(classroom);
					classroomListModel.addElement(classroom.getName());
				}
			}
		});

		editClassroomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = classroomList.getSelectedIndex();
				if (i < 0 ) {
					return;
				}
				String classroomName = classroomListModel.get(i);
				Classroom classroom = sqlManager.getClassroom(classroomName);
				Classroom newClassroom = getClassroomInfo(classroom);
				if (newClassroom != null) {
					sqlManager.updateClassroom(newClassroom);
				}
			}
		});

		deleteClassroomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = classroomList.getSelectedIndex();
				if (i < 0 ) {
					return;
				}
				String classroomName = classroomListModel.get(i);
				classroomListModel.remove(i);
				sqlManager.deleteClassroom(classroomName);
			}
		});

		classroomList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		classroomList.setLayoutOrientation(JList.VERTICAL);
		classroomList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(classroomList);
		listScroller.setPreferredSize(new Dimension(200, 200));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(cmdPanel, gbc);

		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1.0;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(listScroller, gbc);
	}
	
	void addClassStudents() {
		classStudents = sqlManager.getStudentsInClassroom(null);

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(2, 1));
		cmdPanel.add(addStudentToClassroom);
		cmdPanel.add(removeStudentFromClassroom);

		classStudentListModel = new DefaultListModel<String>();
		if (classStudents != null) {
			for(Student student : classStudents) {
				classStudentListModel.addElement(student.getName());
			}
		}
		classStudentList = new JList<String>(classStudentListModel);
		
		addStudentToClassroom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		removeStudentFromClassroom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		classStudentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		classStudentList.setLayoutOrientation(JList.VERTICAL);
		classStudentList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(classStudentList);
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

	void addStudents() {
		
		allStudents = sqlManager.getAllStudents();

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 1));
		cmdPanel.add(addStudentButton);
		cmdPanel.add(editStudentButton);
		cmdPanel.add(deleteStudentButton);

		studentListModel = new DefaultListModel<String>();
		if (allStudents != null) {
			for(Student student : allStudents) {
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
				}
			}
		});

		editStudentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = studentList.getSelectedIndex();
				if (i < 0 ) {
					return;
				}
				String studentName = studentListModel.get(i);
				Student student = sqlManager.getStudent(studentName);
				Student newStudent = getStudentInfo(student);
				if (newStudent != null) {
					sqlManager.updateStudent(newStudent);
				}
			}
		});

		deleteStudentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = studentList.getSelectedIndex();
				if (i < 0 ) {
					return;
				}
				String studentName = studentListModel.get(i);
				studentListModel.remove(i);
				sqlManager.deleteStudent(studentName);
			}
		});

		studentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		studentList.setLayoutOrientation(JList.VERTICAL);
		studentList.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(studentList);
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
	
	private Classroom getClassroomInfo(Classroom classroom) {
		JTextField nameText = new JTextField();
		JTextField periodText = new JTextField();
		JTextField teacherText = new JTextField();

		if (classroom != null) {
			nameText.setText(classroom.getName());

			periodText.setText(classroom.getPeriod() + "");
			teacherText.setText(classroom.getTeacher());
		}

		final JComponent[] inputs = new JComponent[] { new JLabel("Name"), nameText,
				new JLabel("Period"), periodText, new JLabel("Teacher"), teacherText };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Classroom Info",
				  JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String name = nameText.getText().trim();
				int period = Integer.parseInt(periodText.getText().trim());
				String teacher = teacherText.getText().trim();
				if (classroom == null) {
					classroom = new Classroom(name);
				} else {
					classroom.setPeriod(period);
					classroom.setTeacher(teacher);
				}

				return classroom;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;

	}

}
