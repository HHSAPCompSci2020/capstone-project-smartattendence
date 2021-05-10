import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_objdetect.CASCADE_SCALE_IMAGE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

public class PhotoShoot extends WebCam {
	final static int GRID_WIDTH = 4;
	final static int GRID_HEIGHT = 4;
	final static int GRID_SIZE = GRID_WIDTH*GRID_HEIGHT;

	static JLabel[] jlabels = new JLabel[GRID_SIZE];
	static JTextField[] textLabels = new JTextField[GRID_SIZE];
	static Mat[] faceImages = new Mat[GRID_SIZE];
	static boolean snapped = false;
	static JButton snapButton = new JButton("Snap");
	static JButton saveButton = new JButton("Save");
    boolean capturing = false;
    boolean startFaceDetect = false;
	
	PhotoShoot(String dataDir) {
		super(dataDir);
		this.dataDir = dataDir;

		setLayout(new BorderLayout());
        vidpanel = new JLabel();
	    vidpanel.setPreferredSize(new Dimension(600, 300));
	    
	    add(vidpanel, BorderLayout.LINE_START);
         
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new GridLayout(GRID_WIDTH, GRID_HEIGHT));
        snapButton.addActionListener(new ActionListener() {

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
			}
		});
        
        saveButton.setEnabled(false);
        
        JPanel cmdPanel = new JPanel();
        cmdPanel.setLayout(new GridLayout(4, 1));
        cmdPanel.add(snapButton);
        cmdPanel.add(saveButton);
        
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
			Image scaledImage = Mat2BufferedImage(resized).getScaledInstance(jlabels[index+1].getWidth(),
				-1, Image.SCALE_FAST);
			jlabels[index+1].setIcon(new ImageIcon(scaledImage));
			faceImages[index+1] = resized;
		}

	}
	
	public void startCapture() {
		startFaceDetect = false;
		snapped = false;
		super.startCapture();
	}
	
	public void stopCapture() {
		startFaceDetect = false;
		snapped = false;
		super.stopCapture();
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
			if (!isSnapped) {
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
				if (snapped){
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
}