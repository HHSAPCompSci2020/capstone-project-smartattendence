
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is the main class and creates the window.
 * 
 * @author Arya Khokhar and Xinyu Zhao
 * @version 5
 */
public class Main {
	private static SQLiteManager sqlManager;
	protected static String dataDir;

	private static PhotoShoot photoPane;
	private static Attendance attendancePane;
	private static JPanel recordsPane, studentPane;

	private static int attendancePaneIndex, photoPaneIndex, recordsPaneIndex, studentPaneIndex;

	/**
	 * This method is the main method and creates the windows and tabs that are
	 * displayed.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		createDataDirectory();

		sqlManager = new SQLiteManager(dataDir);
		sqlManager.initTables();

		JFrame jframe = new JFrame("Attendence System: Homestead High School");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setPreferredSize(new Dimension(1400, 800));

		JTabbedPane tabbedPane = new JTabbedPane();
		jframe.getContentPane().add(tabbedPane);
		Font font = new Font("Arial", Font.CENTER_BASELINE, 20);
		tabbedPane.setFont(font);

		int startIndex = 0;

		attendancePaneIndex = startIndex++;
		attendancePane = new Attendance(dataDir, sqlManager);
		tabbedPane.addTab(" Attendence ", null, attendancePane, "Take attendence");

		recordsPaneIndex = startIndex++;
		recordsPane = new RecordsPanel(dataDir, sqlManager);
		tabbedPane.addTab(" Records ", null, recordsPane, "View attendence records");

		photoPaneIndex = startIndex++;
		photoPane = new PhotoShoot(dataDir, sqlManager);
		tabbedPane.addTab(" PhotoShoot ", null, photoPane, "Take student pictures");

		studentPaneIndex = startIndex++;
		studentPane = new StudentPanel(dataDir, sqlManager);
		tabbedPane.addTab(" Student ", null, studentPane, "Manage students");

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// System.out.println("Selected tab = " + tabbedPane.getSelectedIndex());
				if (tabbedPane.getSelectedIndex() == photoPaneIndex) {
					attendancePane.stopCapture();
					photoPane.startCapture();
				}
				if (tabbedPane.getSelectedIndex() == attendancePaneIndex) {
					photoPane.stopCapture();
					attendancePane.startCapture();
				} else {
					attendancePane.stopCapture();
					photoPane.stopCapture();
				}
			}
		});

		jframe.setVisible(true);
		jframe.pack();

		tabbedPane.setSelectedIndex(-1);
		tabbedPane.setSelectedIndex(attendancePaneIndex);
	}

	/**
	 * This method creates the data directory within resources.
	 * 
	 * @return path to directory
	 */
	protected static String createDataDirectory() {
		String basePath = System.getProperty("user.dir");
		File dataDirFile = new File(basePath + File.separator + "resources");
		dataDir = dataDirFile.getPath();
		if (!dataDirFile.exists()) {
			dataDirFile.mkdir();
			File classifierDir = new File(dataDirFile + File.separator + "classifiers");
			if (!classifierDir.exists()) {
				classifierDir.mkdir();
			}
			String classifierPath1 = classifierDir.getAbsolutePath() + File.separator
					+ "haarcascade_frontalface_alt.xml";
			saveResource("haarcascade_frontalface_alt.xml", classifierPath1);

			File faceDir = new File(dataDirFile + File.separator + "faces");
			if (!faceDir.exists()) {
				faceDir.mkdir();
			}
		}

		return dataDir;
	}

	/**
	 * This method saves the path to the data folder.
	 * 
	 * @param resourcePath is the path to the resource folder
	 * @param savePath     is used to replace the the new location
	 */
	protected static void saveResource(String resourcePath, String savePath) {
		Main m = new Main();
		ClassLoader classLoader = m.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
		File saveFile = new File(savePath);
		try {
			Files.copy(inputStream, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
