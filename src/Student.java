import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class Student extends JPanel {
	DefaultListModel<String> listModel;
	JList<String> studentList;
	private SQLiteManager sqlManager;
	private String dataDir;

	private final int STUDENT_ID;
	private final String STUDENT_NAME;
	private Image photo;
	private boolean attendance = false;

	GridBagConstraints constraints = new GridBagConstraints();

	static JButton addButton = new JButton("Add Student");
	static JButton editButton = new JButton("Edit Student");
	static JButton deleteButton = new JButton("Delete Student");

	public Student() {
		STUDENT_ID = 0;
		STUDENT_NAME = "";
		photo = null;
	}

	public Student(String dataDir, SQLiteManager sqlManager, int id, String name, Image i) {
		STUDENT_ID = id;
		STUDENT_NAME = name;
		photo = i;
		this.dataDir = dataDir;
		this.sqlManager = sqlManager;

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 1));
		cmdPanel.add(addButton);
		cmdPanel.add(editButton);
		cmdPanel.add(deleteButton);

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		listModel = new DefaultListModel<String>();
		studentList = new JList<String>(listModel);

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

	/**
	 * This method returns the ID of the student
	 * 
	 * @return int ID
	 */
	public int getID() {
		return STUDENT_ID;
	}

	/**
	 * This method return the name of the student
	 * 
	 * @return String name
	 */
	public String getName() {
		return STUDENT_NAME;
	}

	/**
	 * This method returns the attendance of the student on a given day
	 * 
	 * @return true is student was present and false is they were absent
	 */
	public boolean getAttendance() {
		return attendance;
	}

	/**
	 * This method adds a photo to go with the student
	 * 
	 * @param takes in an image
	 */
	public void addPhoto(Image i) {
		photo = i;

	}

}
