
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import java.nio.IntBuffer;
import java.nio.file.Paths;

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
public class Attendence extends WebCam {

	EigenFaceRecognizer efr;
	LBPHFaceRecognizer lfr;
	FisherFaceRecognizer ffr;
	Map<Integer, String> nameMap = new HashMap<Integer, String>();

	String trainingDataFile;
	String nameMapDataFile;

	String classRoom;

	boolean takingAttendence = false;
	DefaultListModel<String> listModel = new DefaultListModel<String>();
	JList<String> list;

	static JButton startButton = new JButton("Start");
	static JButton stopButton = new JButton("Stop");
	static JButton resetButton = new JButton("Reset");
	static JButton saveButton = new JButton("Save");

	int count = 1;

	/**
	 * Attendance creates buttons and panel for the Attendance page.
	 * @param String dataDir: original Data
	 */
	public Attendence(String dataDir) {

		super(dataDir);

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
		cmdPanel.setLayout(new GridLayout(2, 2));
		cmdPanel.add(startButton);
		cmdPanel.add(stopButton);
		cmdPanel.add(resetButton);
		cmdPanel.add(saveButton);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendence = true;
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendence = false;
			}
		});

		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takingAttendence = false;
				listModel.clear();
			}
		});

		listModel.addElement("one");
		listModel.addElement("two");
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 80));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;

		c.gridx = 0;
		c.gridy = 0;
		rightPanel.add(cmdPanel, c);

		c.gridy = 1;
		c.ipady = 500;
		c.ipadx = 200;
		rightPanel.add(listScroller, c);// , BorderLayout.PAGE_END);

		add(rightPanel, BorderLayout.LINE_END);
	}

	/**
	 * This method processes the faces from the camera and adds them
	 * @param int  index: number of faces
	 * @param Mat  grayFrame: matrix of image
	 * @param Rect rect: 
	 */
	public void processFaceRect(int index, Mat grayFrame, Rect rect) {

		if (index < 0 || !takingAttendence) {
			return;
		}

		Mat resized = getResizedImage(grayFrame, rect);
		String faceName = predict(resized);
		if (faceName != null && faceName.length() > 0 && !listModel.contains(faceName)) {
			listModel.addElement(faceName);
		}
	}

	/**
	 * This method starts the video capture when the program runs
	 * @param String classRoom: the classroom that you start the capture in
	 */
	public void startCapture(String classRoom) {
		super.stopCapture();
		this.classRoom = classRoom;
		try {
			createTrainingList();
			train();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.startCapture();
	}

	/**
	 * this method stops the video capture. no parameters are needed.
	 */
	public void stopCapture() {
		takingAttendence = false;
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
	 * @param mat : represents the input image
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
	 * @param ArrayList<Mat>     images: images captured of the students
	 * @param ArrayList<Integer> labels: what their corresponding names are
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
			faceNames.add(faceDir.getName());
			List<String> imgFiles = new ArrayList<String>();
			for (File imgFile : faceDir.listFiles()) {
				if (imgFile.getAbsolutePath().endsWith(".pgm")) {
					imgFiles.add(imgFile.getAbsolutePath());
				}
			}
			faceFiles.add(imgFiles);
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
}
