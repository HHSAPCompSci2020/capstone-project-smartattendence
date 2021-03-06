import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_objdetect.CASCADE_SCALE_IMAGE;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

/**
 * This class is extends JPanel and is responsible for the video camera feed in
 * different tabs of the window.
 * 
 * @author Arya Khokhar
 * @version 6
 */
public class WebCam extends JPanel {

	protected String classifierPath;
	protected String dataDir;

	private boolean capturing = false;
	protected JLabel vidpanel;

	/**
	 * This constructor sets the string parameter as the path to the resources
	 * folder where the machine learning data is stored.
	 * 
	 * @param dataDir is the location of the resource folder
	 */
	public WebCam(String dataDir) {
		super(false);
		this.dataDir = dataDir;
		classifierPath = Paths.get(dataDir, "classifiers", "haarcascade_frontalface_alt.xml").toString();
	}

	/**
	 * This method represents the camera. It processing the frames from the JPanel
	 * and updates the live feed.
	 */
	public void startCapture() {

		Runnable cameraTask = new Runnable() {
			@Override
			public void run() {
				Mat frame = new Mat();
				VideoCapture camera = new VideoCapture(0);

				capturing = true;
				while (capturing) {
					if (camera.read(frame)) {
						try {
							processFrame(frame);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (vidpanel != null) {
							Image scaledImage = Mat2BufferedImage(frame).getScaledInstance(600, -1,
									Image.SCALE_FAST);

							vidpanel.setIcon(new ImageIcon(scaledImage));
						}
					}
				}

				camera.close();
				capturing = false;
			}
		};

		new Thread(cameraTask).start();

	}

	/**
	 * This method stops the camera feed.
	 */
	public void stopCapture() {
		capturing = false;
	}

	/**
	 * This method detects any faces within the camera.
	 * 
	 * @param frame matrix that represents an image
	 * @throws IOException throws exception if error occurs during processing
	 */
	public void processFrame(Mat frame) throws IOException {
		RectVector faces = new RectVector();
		Mat grayFrame = new Mat();
		int absoluteFaceSize = 0;
		CascadeClassifier faceCascade = new CascadeClassifier();

		faceCascade.load(classifierPath);
		opencv_imgproc.cvtColor(frame, grayFrame, COLOR_BGR2GRAY);
		opencv_imgproc.equalizeHist(grayFrame, grayFrame);

		int height = grayFrame.rows();
		if (Math.round(height * 0.2f) > 0) {
			absoluteFaceSize = Math.round(height * 0.1f);
		}

		faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | CASCADE_SCALE_IMAGE,
				new Size(absoluteFaceSize, absoluteFaceSize), new Size(height, height));

		Rect[] facesArray = faces.get();
		processFaceRect(-1, null, null);
		for (int i = 0; i < facesArray.length; i++) {
			opencv_imgproc.rectangle(frame, facesArray[i], new Scalar(0, 255, 0, 255), 3, 1, 0);
			Rect rect = facesArray[i];
			processFaceRect(i, grayFrame, rect);
		}
		processFaceRect(-2, null, null);
		faceCascade.close();
	}

	/**
	 * This method processes the faces from the camera and adds them. It is
	 * overridden in its subclasses.
	 * 
	 * @param index number of faces
	 * @param frame matrix of image
	 * @param rect  rectangular coordinates containing face in frame
	 */
	public void processFaceRect(int index, Mat frame, Rect rect) {
	}

	/**
	 * This resizes the image from a square to a rectangle after it has been
	 * detected.
	 * 
	 * @param grayFrame image to be manipulated
	 * @param rect      is the rectangle that fits around the image
	 * @return matrix with new dimensions
	 */
	public Mat getResizedImage(Mat grayFrame, Rect rect) {
		Rect newRect = new Rect();
		if (rect.width() * 112 / 92 > rect.height()) {
			newRect.width(rect.width());
			newRect.x(rect.x());
			newRect.height(rect.width() * 112 / 92);
			newRect.y(rect.y() - (newRect.height() - rect.height()) / 2);
			if (newRect.y() < 0) {
				newRect.y(0);
			}
			if (grayFrame.arrayHeight() < newRect.y() + newRect.height()) {
				newRect.y(grayFrame.arrayHeight() - newRect.height() - 1);
				if (newRect.y() < 0) {
					return null;
				}
			}
		} else {
			newRect.height(rect.height());
			newRect.y(rect.y());
			newRect.width(rect.height() * 92 / 112);
			newRect.x(rect.x() - (newRect.width() - rect.width()) / 2);
			if (newRect.x() < 0) {
				newRect.x(0);
			}
		}

		Mat cropped = new Mat(grayFrame, newRect);

		Size sz = new Size(92, 112);
		Mat resized = new Mat();
		opencv_imgproc.resize(cropped, resized, sz);

		return resized;
	}

	/**
	 * This method converts the image from a matrix to a buffered image for better
	 * processing.
	 * 
	 * @param m Converts Mat m to BufferedImage data type
	 * @return a BufferedImage equivalent of parameter m
	 */
	public BufferedImage Mat2BufferedImage(Mat m) {

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.data().get(b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	/**
	 * This method saves the image under the given name in the resources folder.
	 * 
	 * @param imageName the file name to be created containing the image in m.
	 * @param mat       the image to be saved
	 */
	public void saveImageFile(String imageName, Mat mat) {
		File faceDataDir = new File(dataDir + File.separator + "faces");
		if (!faceDataDir.exists()) {
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
}