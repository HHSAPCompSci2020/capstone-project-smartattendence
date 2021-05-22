
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
 * @author Arya Khokhar
 * @version 4
 */
public class Main
{
	static SQLiteManager sqlManager;
	static String dataDir;
	
	static PhotoShoot photoPane;
	static Attendance attendancePane;
	static JPanel recordsPane, studentPane;
	
	static int attendancePaneIndex, photoPaneIndex, recordsPaneIndex, studentPaneIndex;
	
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
        tabbedPane.addTab(" Attendence ", null, attendancePane,
                "Take attendence");

        photoPaneIndex = startIndex++;
        photoPane = new PhotoShoot(dataDir, sqlManager);
        tabbedPane.addTab(" PhotoShoot ", null, photoPane,
                "Take student pictures");
        
        recordsPaneIndex = startIndex++;
        recordsPane = new RecordsPanel(dataDir, sqlManager);
        tabbedPane.addTab(" Records ", null, recordsPane,
                "View attendence records");
        
        studentPaneIndex = startIndex++;
        studentPane = new StudentPanel(dataDir, sqlManager);
        tabbedPane.addTab(" Student ", null, studentPane,
                "Manage students");

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	System.out.println("Selected tab = " + tabbedPane.getSelectedIndex());
                if(tabbedPane.getSelectedIndex() == photoPaneIndex) {
                	attendancePane.stopCapture();
                	photoPane.startCapture();
                } if (tabbedPane.getSelectedIndex() == attendancePaneIndex) {
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
        tabbedPane.setSelectedIndex(studentPaneIndex);
	}
	
	static String createDataDirectory() {
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
			saveResource("haarcascade_frontalface_alt.xml",
					classifierPath1);

			File faceDir = new File(dataDirFile + File.separator + "faces");
			if (!faceDir.exists()) {
				faceDir.mkdir();
			}
		}
		
		return dataDir;
	}
	
    static void saveResource(String resourcePath, String savePath) {
    	System.out.println(resourcePath + ", " + savePath);
    	Main m = new Main();
        ClassLoader classLoader = m.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
        System.out.println("resource dir " + inputStream);
	    File saveFile = new File(savePath);
	    try {
			Files.copy(inputStream, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


}
