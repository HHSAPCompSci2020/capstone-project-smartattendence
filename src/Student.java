import java.awt.Image;

/**
 * This class represent a Student.
 * @author Xinyu Zhao
 *
 */
public class Student {

	private final int STUDENT_ID;
	private final String STUDENT_NAME;
	private boolean attendance = false;
	private int grade;

	public Student() {
		STUDENT_ID = 0;
		STUDENT_NAME = "";
	}

	public Student(int id, String name) {
		STUDENT_ID = id;
		STUDENT_NAME = name;
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
	 * This method returns the attendence of the student on a given day
	 * @return true is student was present and false is they were absent
	 */
	public boolean getAttendance(){
		return attendance;
	}


	public int getGrade() {
		return grade;
	}


	public void setGrade(int grade) {
		grade = this.getGrade();
	}


}
