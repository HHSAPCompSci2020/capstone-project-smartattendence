import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class StudentPanel extends JPanel {
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
}
