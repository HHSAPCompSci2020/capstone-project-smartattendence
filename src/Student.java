/**
 * This class represent a Student.
 * 
 * @author Xinyu Zhao
 *
 */
public class Student {

	private final int STUDENT_ID;
	private final String STUDENT_NAME;
	private boolean attendance = false;

	public Student() {
		STUDENT_ID = 0;
		STUDENT_NAME = "";
	}

	public Student(int id, String name) {
		STUDENT_ID = id;
		STUDENT_NAME = name;
	}

	public int getID() {
		return STUDENT_ID;
	}

	public String getName() {
		return STUDENT_NAME;
	}

	public boolean getAttendance(){
		return attendance;
	}

	public void addPhoto(){
		//adds photo
	}

}
