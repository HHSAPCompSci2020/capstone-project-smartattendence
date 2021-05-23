import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_objdetect.CASCADE_SCALE_IMAGE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.leptonica.dealloc_fn;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * This class represents the PhotoShoot panel of the window and uses the WebCam.
 * It adds images of new/existing students to the machine learning models.
 * 
 * @author Arya Khokhar
 * @version 6
 */
public class PhotoShoot extends WebCam {
	private final static int GRID_WIDTH = 4;
	private final static int GRID_HEIGHT = 4;
	private final static int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;

	private static JLabel[] jlabels = new JLabel[GRID_SIZE];
	private static JComboBox[] textLabels = new JComboBox[GRID_SIZE];
	private static Mat[] faceImages = new Mat[GRID_SIZE];
	private static boolean snapped = false;
	private static JButton snapButton = new JButton("Take Picture");
	private static JButton saveButton = new JButton("Save Picture");
	private boolean capturing = false;
	private boolean startFaceDetect = false;

	private SQLiteManager sqlManager;

	private List<Student> allStudents;

	/**
	 * This constructor creates the files in the faces folder for machine learning
	 * and sets up the GUI components of the panel. It also contains the code for
	 * what to do when each button is pressed, including saving the images to the
	 * database.
	 * 
	 * @param dataDir    is the path to the directory that is passes to WebCam
	 * @param sqlManager is the database object
	 */
	public PhotoShoot(String dataDir, SQLiteManager sqlManager) {
		super(dataDir);
		this.sqlManager = sqlManager;

		setLayout(new GridBagLayout());
		vidpanel = new JLabel("     Please wait while video is loading...");
		vidpanel.setPreferredSize(new Dimension(600, 300));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(vidpanel, gbc);

		snapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < GRID_SIZE; ++i) {
					jlabels[i].setIcon(null);
					textLabels[i].setSelectedIndex(0);
					faceImages[i] = null;
				}
				snapped = true;
				saveButton.setEnabled(true);
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < GRID_SIZE; ++i) {
					if (faceImages[i] != null && textLabels[i] != null) {
						int index = textLabels[i].getSelectedIndex();
						if (index == 0) {
							continue;
						}
						Student student = allStudents.get(index - 1);
						String imageName = student.getName() + "__" + student.getID();
						if (imageName != null && imageName.trim().length() > 0) {
							imageName = imageName.trim().toLowerCase();
							try {
								saveImageFile(imageName, faceImages[i]);
								jlabels[i].setIcon(null);
								textLabels[i].setSelectedIndex(0);
								faceImages[i] = null;
							} catch (Exception err) {
								err.printStackTrace();
							}
						}
					}
				}
			}
		});

		saveButton.setEnabled(false);

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new GridLayout(1, 2));
		cmdPanel.add(snapButton);
		cmdPanel.add(saveButton);

		JPanel rightPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		rightPanel.add(cmdPanel, c);

		JPanel iconPanel = new JPanel();
		iconPanel.setLayout(new GridLayout(GRID_WIDTH, GRID_HEIGHT));

		for (int i = 0; i < GRID_SIZE; ++i) {
			JLabel l = new JLabel();
			l.setPreferredSize(new Dimension(92, 112));
			jlabels[i] = l;
			JPanel imagePanel = new JPanel();
			imagePanel.setLayout(new BorderLayout());
			imagePanel.add(l, BorderLayout.NORTH);
			textLabels[i] = new JComboBox<String>();
			textLabels[i].setEditable(true);

			imagePanel.add(textLabels[i], BorderLayout.SOUTH);
			iconPanel.add(imagePanel);
		}

		c.gridy = 1;
		c.insets = new Insets(50, 0, 0, 0);
		rightPanel.add(iconPanel, c);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;

		add(rightPanel, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 2;
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
	 * This method processes the faces from the camera and adds them to the machine
	 * learning files.
	 * 
	 * @param index     number of faces
	 * @param grayFrame matrix of image
	 * @param rect      rectangular coordinates containing face in frame
	 */
	public void processFaceRect(int index, Mat grayFrame, Rect rect) {

		if (index == -1 && startFaceDetect == false) {
			startFaceDetect = true;
			return;
		}

		if (index == -2) {
			startFaceDetect = false;
			snapped = false;
			return;
		}

		Mat resized = getResizedImage(grayFrame, rect);

		if (snapped) {
			System.out.println("taking picture " + index);
			Image scaledImage = Mat2BufferedImage(resized).getScaledInstance(jlabels[index].getWidth(), -1,
					Image.SCALE_FAST);
			jlabels[index].setIcon(new ImageIcon(scaledImage));
			faceImages[index] = resized;
		}

	}

	/**
	 * This method starts the camera and also is responsible for the
	 * grid of assigning names to images on the right of the panel.
	 */
	public void startCapture() {
		startFaceDetect = false;
		snapped = false;
		allStudents = sqlManager.getAllStudents();

		for (int i = 0; i < GRID_SIZE; ++i) {
			DefaultComboBoxModel<String> studentListModel = new DefaultComboBoxModel<String>();
			if (allStudents != null) {
				studentListModel.addElement("--select--");
				for (Student student : allStudents) {
					studentListModel.addElement(student.getName());
				}
			}
			textLabels[i].setModel(studentListModel);
			AutoCompleteDecorator.decorate(textLabels[i]);
		}

		super.startCapture();
	}

	/**
	 * This method stops the camera.
	 */
	public void stopCapture() {
		startFaceDetect = false;
		snapped = false;
		super.stopCapture();
	}

	/**
	 * This method represents the message displayed when a user clicks
	 * on the help button and walks them through how to use the panel.
	 */
	private void showHelp() {
		String msg = "<html><body><h2>PhotoShoot Panel:</h2>"
				+ "You can have multiple students in the camera feed at one time and add all<br>"
				+ "of them at once.<br>" + 
				"<h4>In order to add photos to an existing student or new student into the database:</h4>" + 
				"1) Bring the student(s) into the camera view and click on take picture.<br>" + 
				"2) Once the picture is taken, you can assign a student's name under their<br> "
				+ "   photo by typing it out and selecting it.<br>" + 
				"3) Once the pictures you want to save have a name, you can push save and only<br>"
				+ "   those will be saved.<br>"
				+ "</body></html>";
	
		JOptionPane.showMessageDialog(this, msg, "Attendence Help", JOptionPane.PLAIN_MESSAGE);

	}
}
