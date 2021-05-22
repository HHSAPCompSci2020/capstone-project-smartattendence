
import java.awt.BorderLayout;
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
 * This class is responsible for recognizing faces.
 * @author Arya Khokhar
 * @version 4
 */
public class Attendance extends WebCam {

	EigenFaceRecognizer efr;
	LBPHFaceRecognizer lfr;
	FisherFaceRecognizer ffr;
	Map<Integer, String> nameMap = new HashMap<Integer, String>();

	String trainingDataFile;
	String nameMapDataFile;

	int classroomIndex = -1;

	boolean takingAttendance = false;
	DefaultListModel<String> listPresentModel = new DefaultListModel<String>();
	JList<String> listPresent;

	DefaultListModel<String> listAbsentModel = new DefaultListModel<String>();
	JList<String> listAbsent;
	
	List<Classroom> allClasses;
	JComboBox<String> classroomCombo;
	DefaultComboBoxModel<String> classroomComboModel = new DefaultComboBoxModel<String>();
	
	List<Student> allClassStudents = new ArrayList<Student>();
	List<Student> presentStudents = new ArrayList<Student>();
	List<Student> absentStudents = new ArrayList<Student>();

	static JButton startButton = new JButton("Start");
	static JButton stopButton = new JButton("Stop");
	static JButton resetButton = new JButton("Reset");
	static JButton saveButton = new JButton("Save");
	
	int count = 1;
	SQLiteManager sqlManager;

	/**
	 * Attendance creates buttons and panel for the Attendance page.
	 * @param dataDir original Data
	 */
	public Attendance(String dataDir, SQLiteManager sqlManager) {

		super(dataDir);
		this.sqlManager = sqlManager;

		nameMapDataFile = Paths.get(dataDir, "faces", "namemap.txt").toString();
		trainingDataFile = Paths.get(dataDir, "faces", "training.txt").toString();

		// testing statements
		System.out.println("nameMapDataFile=" + nameMapDataFile);
		System.out.println("trainingDataFile=" + trainingDataFile);

		setLayout(new BorderLayout());
		vidpanel = new JLabel();
		vidpanel.setPreferredSize(new Dimension(600, 300));

		add(vidpanel, BorderLayout.LINE_START);

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
				for(Student student : allClassStudents) {
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
				for(Student student : presentStudents) {
					sqlManager.saveStudentAttendence(classroomId, student.getID(), date, true);
				}
				for(Student student : absentStudents) {
					sqlManager.saveStudentAttendence(classroomId, student.getID(), date, false);
				}
			}
		});

		listPresent = new JList<String>(listPresentModel);
		listPresent.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listPresent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listPresent.setVisibleRowCount(-1);
		JScrollPane listPresentScroller = new JScrollPane(listPresent);
		listPresentScroller.setBorder(BorderFactory.createTitledBorder ("Present students"));
		listPresentScroller.setPreferredSize(new Dimension(400, 300));

		listAbsent = new JList<String>(listAbsentModel);
		listAbsent.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		listAbsent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listAbsent.setVisibleRowCount(-1);
		JScrollPane listAbsentScroller = new JScrollPane(listAbsent);
		listAbsentScroller.setBorder(BorderFactory.createTitledBorder ("Absent students"));
		listAbsentScroller.setPreferredSize(new Dimension(400, 300));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;

		c.gridx = 0;
		c.gridy = 0;
		rightPanel.add(cmdPanel, c);

		c.gridy = 1;
		//c.ipady = 400;
		//c.ipadx = 200;
		rightPanel.add(listPresentScroller, c);

		c.gridy = 2;
		//c.ipady = 400;
		//c.ipadx = 200;
		rightPanel.add(listAbsentScroller, c);

		add(rightPanel, BorderLayout.LINE_END);
	}

	/**
	 * This method processes the faces from the camera and adds them
	 * @param index number of faces
	 * @param grayFrame matrix of image
	 * @param rect rectangular coordinates containing face in frame
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
				int id = Integer.parseInt(faceName.substring(i+2));
				for(int k = 0; k < absentStudents.size(); k++) {
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
	 * This method starts the video capture when the program runs
	 * @param classRoom the classroom that you start the capture in
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
			for(Student student : allClassStudents ) {
				int index = -1;
				for(int i = 0; i < prevClassStudents.size(); i++) {
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
			for(Student prevStudent : prevClassStudents ) {
				int index = -1;
				for(int i = 0; i < allClassStudents.size(); i++) {
					Student student = allClassStudents.get(i);
					if (student.getID() == prevStudent.getID()) {
						index = i;
						break;
					}
				}
				
				if (index == -1) {
					for(int i = 0; i < absentStudents.size(); i++) {
						Student absent = absentStudents.get(i);
						if (absent.getID() == prevStudent.getID()) {
							absentStudents.remove(i);
							listAbsentModel.remove(i);
							break;
						}
					}
					for(int i = 0; i < presentStudents.size(); i++) {
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
			for(int i = 0; i < allClasses.size(); i++) {
				Classroom c = allClasses.get(i);
				classroomComboModel.addElement(c.getCourseName());
			}
			classroomIndex = savedClassroomIndex;
			classroomCombo.setSelectedIndex(classroomIndex);
		}
	}
	
	/**
	 * this method stops the video capture. no parameters are needed.
	 */
	public void stopCapture() {
		takingAttendance = false;
		super.stopCapture();
	}

	/**
	 * This method trains the model on the data.
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

		System.out.println("Starting training on " + images.size() + " data points ...");

		efr.train(matImages, matLabels);
		lfr.train(matImages, matLabels);
		ffr.train(matImages, matLabels);

		System.out.println("Starting completed...");
		readNameMapData();
	}

	/**
	 * This method predicts which Student in the database matches most closely with
	 * the current photo.
	 * 
	 * @param mat represents the input image
	 * @return The name of the student
	 */
	public String predict(Mat mat) {
		if (efr == null) {
			return null;
		}

		int[] outLabel = new int[1];
		double[] outConf = new double[1];
		// System.out.print("found: ");

		efr.predict(mat, outLabel, outConf);
		String efrName = nameMap.get(outLabel[0]);
		// System.out.print(" E=" + efrName);

		ffr.predict(mat, outLabel, outConf);
		String ffrName = nameMap.get(outLabel[0]);
		// System.out.print(" F=" + efrName);

		lfr.predict(mat, outLabel, outConf);
		String lfrName = nameMap.get(outLabel[0]);
		// System.out.print(" L=" + efrName);

		if (efrName != null && efrName.contentEquals(ffrName) && efrName.equals(lfrName)) {
			System.out.println("Found: " + efrName + "    " + count++);
		}

		return nameMap.get(outLabel[0]);
	}

	/**
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
	 * This method generated the training data that the program will use
	 * to recognize the students
	 * @throws Exception
	 */
	private void createTrainingList() throws Exception {
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
			int id = Integer.parseInt(faceName.substring(i+2));
			boolean studentInClass = false;
			for(Student student : allClassStudents) {
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
	 * reads the data
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
	
	void showHelp() {
		String msg = "<HTML><BODY>Help Message</BODY></HTML>";
		JOptionPane.showMessageDialog(null, msg, "Attendence Help", JOptionPane.PLAIN_MESSAGE);
	}

}
