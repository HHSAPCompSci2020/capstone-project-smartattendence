import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_objdetect.CASCADE_SCALE_IMAGE;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

public class PhotoShoot extends JPanel {
	final static int GRID_WIDTH = 4;
	final static int GRID_HEIGHT = 4;
	final static int GRID_SIZE = GRID_WIDTH*GRID_HEIGHT;

	public String classifierPath;
	public String trainingDataFile;
	public String nameMapDataFile;

	static JLabel[] jlabels = new JLabel[GRID_SIZE];
	static JTextField[] textLabels = new JTextField[GRID_SIZE];
	static Mat[] faceImages = new Mat[GRID_SIZE];
	static boolean snapped = false;
	static JButton snapButton = new JButton("Snap");
	static JButton saveButton = new JButton("Save");
	static JButton detectButton = new JButton("Detection");
	static JButton recognizeButton = new JButton("Recognition");
	static boolean recognitionMode;
	static FaceRecognition faceRecognition;
    String dataDir;
    boolean capturing = false;
    JLabel vidpanel;
	
	PhotoShoot(String dataDir) {
		super(false);
		this.dataDir = dataDir;

		classifierPath = Paths.get(dataDir, "classifiers", "haarcascade_frontalface_alt.xml").toString();
		nameMapDataFile = Paths.get(dataDir, "faces", "namemap.txt").toString();
		trainingDataFile = Paths.get(dataDir, "faces", "training.txt").toString();
		
		System.out.println("datadir=" + dataDir);
		System.out.println("classifierPath1=" + classifierPath);
		System.out.println("nameMapDataFile=" + nameMapDataFile);
		System.out.println("trainingDataFile=" + trainingDataFile);
		
        setLayout(new BorderLayout());
        vidpanel = new JLabel();
	    vidpanel.setPreferredSize(new Dimension(600, 300));
	    
	    add(vidpanel, BorderLayout.LINE_START);
         
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new GridLayout(GRID_WIDTH, GRID_HEIGHT));
        snapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 1; i < GRID_SIZE; ++i) {
					jlabels[i].setIcon(null);
					textLabels[i].setText("");
					faceImages[i] = null;
				}
				snapped = true;
				saveButton.setEnabled(true);
			}
		});
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 1; i < GRID_SIZE; ++i) {
					if (faceImages[i] != null && textLabels[i] != null) {
						String imageName = textLabels[i].getText();
						if (imageName != null && imageName.trim().length() > 0) {
							imageName = imageName.trim().toLowerCase();
							try {
								saveImageFile(imageName, faceImages[i]);
								jlabels[i].setIcon(null);
								textLabels[i].setText("");
								faceImages[i] = null;
							} catch (Exception err) {
								err.printStackTrace();
							}
						}
					}
				}
				try {
					createTrainingList();
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		});
        
        detectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        detectButton.setEnabled(false);
		        recognizeButton.setEnabled(true);
		        snapButton.setEnabled(true);
		        recognitionMode = false;
			}
		});
        
        recognizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        detectButton.setEnabled(true);
		        recognizeButton.setEnabled(false);
		        snapButton.setEnabled(false);
		        saveButton.setEnabled(false);
		        faceRecognition = new FaceRecognition(trainingDataFile, nameMapDataFile);
		        faceRecognition.train();
		        recognitionMode = true;
			}
		});
        
        recognitionMode = false;
        detectButton.setEnabled(false);
        saveButton.setEnabled(false);
        
        JPanel cmdPanel = new JPanel();
        cmdPanel.setLayout(new GridLayout(4, 1));
        cmdPanel.add(snapButton);
        cmdPanel.add(saveButton);
        cmdPanel.add(detectButton);
        cmdPanel.add(recognizeButton);

    	iconPanel.add(cmdPanel);
    	
        for(int i = 1; i < GRID_SIZE; ++i) {
        	JLabel l = new JLabel();
        	l.setPreferredSize(new Dimension(92, 112));
        	jlabels[i] = l;
            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new BorderLayout());
            imagePanel.add(l, BorderLayout.NORTH);
            textLabels[i] = new JTextField();
            imagePanel.add(textLabels[i], BorderLayout.SOUTH);
        	iconPanel.add(imagePanel);
        }
        
        add(iconPanel, BorderLayout.LINE_END);
	}
	
	public void startCapture() {
		
		Runnable cameraTask = new Runnable() {
			@Override
			public void run() {
				Mat frame = new Mat();
				VideoCapture camera = new VideoCapture(0);
				System.out.println("Starting video capture");

				capturing = true;
				while (capturing) {
					if (camera.read(frame)) {
						try {
							if (snapped) {
								detectFace(frame, true);
								snapped = false;
							} else {
								detectFace(frame, false);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						Image scaledImage = Mat2BufferedImage(frame).getScaledInstance(vidpanel.getWidth(), -1,
								Image.SCALE_FAST);

						vidpanel.setIcon(new ImageIcon(scaledImage));

					}
				}
				
				camera.close();
				capturing = false;
				System.out.println("Stopped video capture");
			}
		};
		
		new Thread(cameraTask).start();

	}
	
	public void stopCapture() {
		System.out.println("Stopping video capture");
		capturing = false;
	}
	
	/**
	 * This method detects any faces within the camera.
	 * @param frame
	 * @param isSnapped
	 * @throws IOException
	 */
	public void detectFace(Mat frame, boolean isSnapped) throws IOException
	{
		RectVector faces = new RectVector();
		Mat grayFrame = new Mat();
		int absoluteFaceSize=0;
		CascadeClassifier faceCascade = new CascadeClassifier();
		
		faceCascade.load(classifierPath);
		opencv_imgproc.cvtColor(frame, grayFrame, COLOR_BGR2GRAY);
		opencv_imgproc.equalizeHist(grayFrame, grayFrame);
		
		int height = grayFrame.rows();
		if (Math.round(height * 0.2f) > 0) {
			absoluteFaceSize = Math.round(height * 0.1f);
		}
				
		faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | CASCADE_SCALE_IMAGE,
				new Size(absoluteFaceSize, absoluteFaceSize), new Size(height,height));
				
		Rect[] facesArray = faces.get();
		for (int i = 0; i < facesArray.length; i++) {
			opencv_imgproc.rectangle(frame, facesArray[i], new Scalar(0, 255, 0, 255), 3, 1, 0);
			if (!isSnapped && !recognitionMode) {
				continue;
			}
			Rect rect = facesArray[i];
			Rect newRect = new Rect();
			if (rect.width()*112/92 > rect.height()) {
				newRect.width(rect.width());
				newRect.x(rect.x());
				newRect.height(rect.width() * 112 / 92);
				newRect.y(rect.y() - (newRect.height() - rect.height())/2);
				if (newRect.y() < 0) {
					newRect.y(0);
				}
				if (frame.arrayHeight() < newRect.y() + newRect.height()) {
					newRect.y(frame.arrayHeight() - newRect.height() - 1);
					if (newRect.y() < 0) {
						continue;
					}
				}
			} else {
				newRect.height(rect.height());
				newRect.y(rect.y());
				newRect.width(rect.height() * 92 / 112);
				newRect.x(rect.x() - (newRect.width() - rect.width())/2);
				if (newRect.x() < 0) {
					newRect.x(0);
				}
			}
			
			try {
				Mat cropped = new Mat(grayFrame, newRect);
				Size sz = new Size(92,112);
				Mat resized = new Mat();
				opencv_imgproc.resize(cropped, resized, sz);
				if (recognitionMode && faceRecognition != null) {
					String faceName = faceRecognition.predict(resized);
					//System.out.println("Found: " + faceName);
				} else if (snapped){
					Image scaledImage = Mat2BufferedImage(resized).getScaledInstance(jlabels[i+1].getWidth(),
	            		-1, Image.SCALE_FAST);
					jlabels[i+1].setIcon(new ImageIcon(scaledImage));
					faceImages[i+1] = resized;
				}
			} catch (Exception e) {
				System.out.println(frame.size() + " [" + rect.x() + ", " + rect.y()
				+ "], "+ " [" + newRect.x() + ", " + newRect.y() + "] " + newRect.size());
				e.printStackTrace();
			}
		}
		faceCascade.close();
	}

	
    public BufferedImage Mat2BufferedImage(Mat m){

     int type = BufferedImage.TYPE_BYTE_GRAY;
     if ( m.channels() > 1 ) {
         type = BufferedImage.TYPE_3BYTE_BGR;
     }
     int bufferSize = m.channels()*m.cols()*m.rows();
     byte [] b = new byte[bufferSize];
     m.data().get(b);
     BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
     final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
     System.arraycopy(b, 0, targetPixels, 0, b.length);  
     return image;
    }
	
	private void saveImageFile(String imageName, Mat mat) {
		File faceDataDir = new File(dataDir + File.separator + "faces");
		if (!faceDataDir.exists()){
			faceDataDir.mkdir();
		}
		
		File nameDir = new File(faceDataDir, imageName);
		if (!nameDir.exists()) {
			nameDir.mkdir();
		}
		
		int seq = 1;
		File imgFile = null;
		while (seq < 100) {
			imgFile = new File(nameDir, "" + seq + ".pgm");
			if (imgFile.exists()) {
				seq++;
				continue;
			}
			break;
		}
		if (imgFile != null) {
			opencv_imgcodecs.imwrite(imgFile.getAbsolutePath(), mat);
		}
		
	}
	
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

	    for(int i = 0; i < faceNames.size(); i++) {
	    	String faceName = faceNames.get(i);
	    	nameMapWriter.append(faceName + ";" + (i+1) + "\n");
	    	List<String> imgFiles = faceFiles.get(i);
	    	for(int f = 0; f < imgFiles.size(); f++) {
	    		String resourceDir = dataDir;
	    		imgPathWriter.append(imgFiles.get(f).substring(resourceDir.length()) + ";" + (i+1) + "\n");
	    	}
	    }
	    nameMapWriter.close();
	    imgPathWriter.close();

	}


}
