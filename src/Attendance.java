
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 * This class is responsible for the attendance panel and respective GUI. It is
 * also responsible for recognizing faces and taking attendance.
 * 
 * @author Arya Khokhar
 * @version 8
 */
public class Attendance extends WebCam {

	// Machine learning models
	private EigenFaceRecognizer efr;
	private LBPHFaceRecognizer lfr;
	private FisherFaceRecognizer ffr;
	private Map<Integer, String> nameMap = new HashMap<Integer, String>();

	private String trainingDataFile;
	private String nameMapDataFile;

	private int classroomIndex = -1;

	private boolean takingAttendance = false;
	private DefaultListModel<String> listPresentModel = new DefaultListModel<String>();
	private JList<String> listPresent;

	private DefaultListModel<String> listAbsentModel = new DefaultListModel<String>();
	private JList<String> listAbsent;

	private List<Classroom> allClasses;
	private JComboBox<String> classroomCombo;
	private DefaultComboBoxModel<String> classroomComboModel = new DefaultComboBoxModel<String>();

	private List<Student> allClassStudents = new ArrayList<Student>();
	private List<Student> presentStudents = new ArrayList<Student>();
	private List<Student> absentStudents = new ArrayList<Student>();

	private static JButton startButton = new JButton("Start");
	private static JButton stopButton = new JButton("Stop");
	private static JButton resetButton = new JButton("Reset");
	private static JButton saveButton = new JButton("Save");

	private int count = 1;
	protected SQLiteManager sqlManager;

	/**
	 * This constructor creates the files in the faces folder for machine learning
	 * and sets up the GUI components of the panel. It also contains the code for
	 * what to do when each button is pressed, including saving the data to the
	 * database.
	 * 
	 * @param dataDir    is the path to the directory that is passes to WebCam
	 * @param sqlManager is the database object
	 */
	public Attendance(String dataDir, SQLiteManager sqlManager) {

		super(dataDir);
		this.sqlManager = sqlManager;

		nameMapDataFile = Paths.get(dataDir, "faces", "namemap.txt").toString();
		trainingDataFile = Paths.get(dataDir, "faces", "training.txt").toString();

		setLayout(new GridBagLayout());
		vidpanel = new JLabel("    Pleae wait while video is loading...");
		vidpanel.setPreferredSize(new Dimension(600, 300));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(vidpanel, gbc);

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(3, 2));
		cmdPanel.add(startButton);
		cmdPanel.add(stopButton);
		cmdPanel.add(resetButton);
		cmdPanel.add(saveButton);

		classroomCombo = new JComboBox<String>(classroomComboModel);
		cmdPanel.add(new JLabel("Classroom"));
		cmdPanel.add(classroomCombo);

		classroomCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newClassroomIndex = classroomCombo.getSelectedIndex();
				if (classroomIndex == -1 || newClassroomIndex == classroomIndex) {
					return;
				}
				classroomIndex = newClassroomIndex;
				int classroomId = allClasses.get(classroomIndex).getId();
				List<Integer> studentIds = sqlManager.getStudentsInClassroom(classroomId);
				if (studentIds != null) {
					allClassStudents.clear();
					absentStudents.clear();
					listAbsentModel.removeAllElements();
					presentStudents.clear();
					listPresentModel.removeAllElements();
					for (Integer id : studentIds) {
						Student student = sqlManager.getStudent(id);
						allClassStudents.add(student);
						absentStudents.add(student);
						listAbsentModel.addElement(student.getName());
					}
				}
			}

		});

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendance = true;
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendance = false;
			}
		});

		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendance = false;
				listPresentModel.clear();
				listAbsentModel.clear();
				presentStudents.clear();
				absentStudents.clear();
				for (Student student : allClassStudents) {
					absentStudents.add(student);
					listAbsentModel.addElement(student.getName());
				}
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendance = false;
				int classroomIndex = classroomCombo.getSelectedIndex();
				Classroom classroom = allClasses.get(classroomIndex);
				int classroomId = classroom.getId();
				Date date = new Date();
				for (Student student : presentStudents) {
					sqlManager.saveStudentAttendence(classroomId, student.getID(), date, true);
				}
				for (Student student : absentStudents) {
					sqlManager.saveStudentAttendence(classroomId, student.getID(), date, false);
				}
			}
		});

		listPresent = new JList<String>(listPresentModel);
		listPresent.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listPresent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listPresent.setVisibleRowCount(-1);
		JScrollPane listPresentScroller = new JScrollPane(listPresent);
		listPresentScroller.setBorder(BorderFactory.createTitledBorder("Present students"));
		listPresentScroller.setPreferredSize(new Dimension(400, 300));

		listAbsent = new JList<String>(listAbsentModel);
		listAbsent.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listAbsent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listAbsent.setVisibleRowCount(-1);
		JScrollPane listAbsentScroller = new JScrollPane(listAbsent);
		listAbsentScroller.setBorder(BorderFactory.createTitledBorder("Absent students"));
		listAbsentScroller.setPreferredSize(new Dimension(400, 300));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;

		c.gridx = 0;
		c.gridy = 0;
		rightPanel.add(cmdPanel, c);

		c.gridy = 1;
		rightPanel.add(listPresentScroller, c);

		c.gridy = 2;
		rightPanel.add(listAbsentScroller, c);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(rightPanel, gbc);

		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;

		JButton helpButton = new JButton("Help");
		helpButton.setBackground(Color.GREEN);
		helpButton.setOpaque(true);
		rightPanel.add(helpButton, c);

		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHelp();
			}
		});
	}

	/**
	 * This method processes the faces from the camera and adds them to the machine
	 * learning files. It overrides the method in its superclass. 
	 * 
	 * @param index     number of faces
	 * @param grayFrame matrix of image
	 * @param rect      rectangular coordinates containing face in frame
	 */
	public void processFaceRect(int index, Mat grayFrame, Rect rect) {

		if (index < 0 || !takingAttendance) {
			return;
		}

		Mat resized = getResizedImage(grayFrame, rect);
		String faceName = predict(resized);
		if (faceName != null && faceName.length() > 0 && !listPresentModel.contains(faceName)) {
			try {
				int i = faceName.indexOf("__");
				if (i == -1) {
					return;
				}
				int id = Integer.parseInt(faceName.substring(i + 2));
				for (int k = 0; k < absentStudents.size(); k++) {
					Student student = absentStudents.get(k);
					if (student.getID() == id) {
						absentStudents.remove(k);
						listAbsentModel.remove(k);
						presentStudents.add(student);
						listPresentModel.addElement(student.getName());
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method starts the video capture when the program runs and creates and
	 * trains the models on the current student list of the selected class.
	 */
	public void startCapture() {
		super.stopCapture();
		try {
			updateStudentAndClasses();
			createTrainingList();
			train();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.startCapture();
	}

	/**
	 * This method stops the video capture.
	 */
	public void stopCapture() {
		takingAttendance = false;
		super.stopCapture();
	}

	/**
	 * This method occurs as soon as the Attendance tab is selected and makes sure
	 * that the information the models have and that the class lists and students in
	 * each class are updated with current information.
	 */
	public void updateStudentAndClasses() {
		allClasses = sqlManager.getAllClassrooms();
		if (classroomIndex == -1 && allClasses != null && allClasses.size() > 0) {
			classroomIndex = 0;
		}

		if (classroomIndex > allClasses.size() - 1) {
			classroomIndex = 0;
		}

		if (classroomIndex >= 0) {
			List<Student> prevClassStudents = new ArrayList<Student>();
			int classroomId = allClasses.get(classroomIndex).getId();
			List<Integer> studentIds = sqlManager.getStudentsInClassroom(classroomId);
			if (studentIds != null) {
				prevClassStudents.addAll(allClassStudents);
				allClassStudents.clear();
				for (Integer id : studentIds) {
					allClassStudents.add(sqlManager.getStudent(id));
				}
			}
			for (Student student : allClassStudents) {
				int index = -1;
				for (int i = 0; i < prevClassStudents.size(); i++) {
					Student prevStudent = prevClassStudents.get(i);
					if (prevStudent.getID() == student.getID()) {
						index = i;
						break;
					}
				}

				if (index == -1) {
					absentStudents.add(student);
					listAbsentModel.addElement(student.getName());
				}
			}
			for (Student prevStudent : prevClassStudents) {
				int index = -1;
				for (int i = 0; i < allClassStudents.size(); i++) {
					Student student = allClassStudents.get(i);
					if (student.getID() == prevStudent.getID()) {
						index = i;
						break;
					}
				}

				if (index == -1) {
					for (int i = 0; i < absentStudents.size(); i++) {
						Student absent = absentStudents.get(i);
						if (absent.getID() == prevStudent.getID()) {
							absentStudents.remove(i);
							listAbsentModel.remove(i);
							break;
						}
					}
					for (int i = 0; i < presentStudents.size(); i++) {
						Student present = presentStudents.get(i);
						if (present.getID() == prevStudent.getID()) {
							presentStudents.remove(i);
							listPresentModel.remove(i);
							break;
						}
					}
				}
			}
		}

		if (allClasses != null) {
			int savedClassroomIndex = classroomIndex;
			classroomIndex = -1;
			classroomComboModel.removeAllElements();
			for (int i = 0; i < allClasses.size(); i++) {
				Classroom c = allClasses.get(i);
				classroomComboModel.addElement(c.getCourseName());
			}
			classroomIndex = savedClassroomIndex;
			classroomCombo.setSelectedIndex(classroomIndex);
		}
	}

	/**
	 * This method trains the models on the data saved.
	 */
	public void train() {
		ArrayList<Mat> images = new ArrayList<>();
		ArrayList<Integer> labels = new ArrayList<>();
		readTrainingData(images, labels);

		if (images.size() == 0) {
			return;
		}

		MatVector matImages = new MatVector(images.size());
		Mat matLabels = new Mat(images.size(), 1, CV_32SC1);
		IntBuffer labelsBuf = matLabels.createBuffer();

		for (int i = 0; i < images.size(); ++i) {
			matImages.put(i, images.get(i));
			labelsBuf.put(i, labels.get(i));
		}

		efr = EigenFaceRecognizer.create();
		lfr = LBPHFaceRecognizer.create();
		ffr = FisherFaceRecognizer.create();

		efr.train(matImages, matLabels);
		lfr.train(matImages, matLabels);
		ffr.train(matImages, matLabels);

		readNameMapData();
	}

	/**
	 * This method predicts which Student in the database matches most closely with
	 * the current photo.
	 * 
	 * @param mat represents the input image
	 * @return the name of the student
	 */
	public String predict(Mat mat) {
		if (efr == null) {
			return null;
		}
		int[] outLabel = new int[1];
		double[] outConf = new double[1];

		efr.predict(mat, outLabel, outConf);
		String efrName = nameMap.get(outLabel[0]);
		ffr.predict(mat, outLabel, outConf);
		String ffrName = nameMap.get(outLabel[0]);
		lfr.predict(mat, outLabel, outConf);
		String lfrName = nameMap.get(outLabel[0]);

		if (efrName != null && efrName.contentEquals(ffrName) && efrName.equals(lfrName)) {
			System.out.println("Found: " + efrName + "    " + count++);
		}
		return nameMap.get(outLabel[0]);
	}

	/**
	 * This method reads the data from both ArrayLists to get which saved images
	 * correspond to which machine learning index.
	 * 
	 * @param images images captured of the students
	 * @param labels what their corresponding names are
	 */
	private void readTrainingData(ArrayList<Mat> images, ArrayList<Integer> labels) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(trainingDataFile));

			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\\;");
				String imgPath = Main.dataDir + tokens[0];
				Mat readImage = opencv_imgcodecs.imread(imgPath, 0);
				images.add(readImage);
				labels.add(Integer.parseInt(tokens[1]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method generates the training data that the program will use to
	 * recognize the students.
	 * 
	 * @throws IOException
	 */
	private void createTrainingList() throws IOException {
		List<String> faceNames = new ArrayList<String>();
		List<List<String>> faceFiles = new ArrayList<List<String>>();
		File faceDataDir = new File(dataDir + File.separator + "faces");
		for (File faceDir : faceDataDir.listFiles()) {
			if (!faceDir.isDirectory()) {
				continue;
			}
			String faceName = faceDir.getName();
			int i = faceName.indexOf("__");
			if (i == -1) {
				continue;
			}

			int id = Integer.parseInt(faceName.substring(i + 2));
			boolean studentInClass = false;
			for (Student student : allClassStudents) {
				if (student.getID() == id) {
					studentInClass = true;
					break;
				}
			}
			if (studentInClass == false) {
				continue;
			}

			List<String> imgFiles = new ArrayList<String>();
			for (File imgFile : faceDir.listFiles()) {
				if (imgFile.getAbsolutePath().endsWith(".pgm")) {
					imgFiles.add(imgFile.getAbsolutePath());
				}
			}

			if (imgFiles.size() > 0) {
				faceNames.add(faceName);
				faceFiles.add(imgFiles);
			}
		}

		File nameMapFile = new File(nameMapDataFile);
		BufferedWriter nameMapWriter = new BufferedWriter(new FileWriter(nameMapFile));

		File trainingFile = new File(trainingDataFile);
		BufferedWriter imgPathWriter = new BufferedWriter(new FileWriter(trainingFile));

		for (int i = 0; i < faceNames.size(); i++) {
			String faceName = faceNames.get(i);
			nameMapWriter.append(faceName + ";" + (i + 1) + "\n");
			List<String> imgFiles = faceFiles.get(i);
			for (int f = 0; f < imgFiles.size(); f++) {
				String resourceDir = dataDir;
				imgPathWriter.append(imgFiles.get(f).substring(resourceDir.length()) + ";" + (i + 1) + "\n");
			}
		}
		nameMapWriter.close();
		imgPathWriter.close();
	}

	/**
	 * This method reads the data in the files that assigns each saved person to a
	 * particular machine learning index.
	 */
	private void readNameMapData() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(nameMapDataFile));

			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\\;");
				if (tokens.length == 2) {
					Integer id = new Integer(tokens[1]);
					nameMap.put(id, tokens[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method represents the help message that displays on the window to help
	 * users understand how to take attendance.
	 */
	private void showHelp() {
		String msg = "<html><h1>Attendence Panel</h1>Before you take the attendence, please make sure that "
				+ " at least two students have taken their pictures in <b>Photoshoot Panel</b>. A "
				+ "student can not be recognized if his/her picture is not available. <h2>Steps to take attendence are:</h2><ul>"
				+ "<li>Select the classroom in ComboBox.</li>"
				+ "<li>Make sure that <b>Absent Student</b> list shows all students in the classroom.</li>"
				+ "<li>Start the attendence by click <b>Start</b> button.</li>"
				+ "<li>During attendence, make sure that recognized students are moved to <b>Present Students</b> list.</li>"
				+ "<li>Once the attendence is over, click <b>Stop</b> to stop the attendence.</li>"
				+ "<li>Save the attendence for the day by click the <b>Save</b> button.</li>"
				+ "<li>You can also reset the attendence by click <b>Reset</b> button.</li>"
				+ "</ul><body></body></html>";
		JOptionPane.showMessageDialog(null, msg, "Attendence Help", JOptionPane.PLAIN_MESSAGE);
	}
}