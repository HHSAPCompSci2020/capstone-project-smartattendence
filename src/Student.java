import java.awt.Image;

/**
 * This class represent a Student.
 * @author Xinyu Zhao
 *
 */
public class Student {

	private final int STUDENT_ID;
	private final String STUDENT_NAME;
	private Image photo;
	private boolean attendance = false;
	
	

	public Student() {
		STUDENT_ID = 0;
		STUDENT_NAME = "";
		photo = null;
	}

	public Student(int id, String name, Image i) {
		STUDENT_ID = id;
		STUDENT_NAME = name;
		photo = i;
	}

	/**
	 * This method returns the ID of the student
	 * @return int ID
	 */
	public int getID() {
		return STUDENT_ID;
	}

	/**
	 * This method return the name of the student
	 * @return String name
	 */
	public String getName() {
		return STUDENT_NAME;
	}

	/**
	 * This method returns the attendance of the student on a given day
	 * @return true is student was present and false is they were absent
	 */
	public boolean getAttendance(){
		return attendance;
	}

	/**
	 * This method adds a photo to go with the student
	 * @param takes in an image
	 */
	public void addPhoto(Image i){
		photo = i;
		
	}

}
