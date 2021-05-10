
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import static org.bytedeco.opencv.global.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is the main class and creates the window.
 * @author Arya Khokhar and Xinyu Zhao
 * @version 2
 */
public class Main
{
	static SQLiteManager sqlManager;
	static String dataDir;
	
	static JPanel photoPane, attendencePane, classroomPane, studentPane;
	
	static int attendencePaneIndex, photoPaneIndex, classroomPaneIndex, studentPaneIndex;
	
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
        
        attendencePaneIndex = startIndex++;
        attendencePane = new JPanel(false);
        attendencePane.setLayout(new GridLayout(1, 1));
        tabbedPane.addTab(" Attendence ", null, attendencePane,
                "Take attendence");

        photoPaneIndex = startIndex++;
        photoPane = new PhotoShoot(dataDir);
        tabbedPane.addTab(" PhotoShoot ", null, photoPane,
                "Take student pictures");
        
        classroomPaneIndex = startIndex++;
        classroomPane = new JPanel(false);
        classroomPane.setLayout(new GridLayout(1, 1));
        tabbedPane.addTab(" Classroom ", null, classroomPane,
                "Manage classrooms");
        
        studentPaneIndex = startIndex++;
        studentPane = new JPanel(false);
        studentPane.setLayout(new GridLayout(1, 1));
        tabbedPane.addTab(" Student ", null, studentPane,
                "Manage students");

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
            	System.out.println("Selected tab = " + tabbedPane.getSelectedIndex());
                if(tabbedPane.getSelectedIndex() == photoPaneIndex) {
                	((PhotoShoot)photoPane).startCapture();
                } else {
                	((PhotoShoot)photoPane).stopCapture();
                }
            }
        });
        
        jframe.setVisible(true);
 	    jframe.pack();

        tabbedPane.setSelectedIndex(-1);
        tabbedPane.setSelectedIndex(photoPaneIndex);
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
