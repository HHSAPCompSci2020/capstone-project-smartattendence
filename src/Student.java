import java.awt.Image;

/**
 * This class represents a Student.
 * 
 * @author Xinyu Zhao
 * @version 5
 */
public class Student {

	private final int STUDENT_ID;
	private final String STUDENT_NAME;
	private boolean attendance = false;
	private int grade;

	/**
	 * This default constructor creates a Student and sets the ID to 0 and the name
	 * to an empty string.
	 */
	public Student() {
		STUDENT_ID = 0;
		STUDENT_NAME = "";
	}

	/**
	 * This constructor creates a student with a given ID and name.
	 * 
	 * @param id   the student's ID number
	 * @param name the student's name
	 */
	public Student(int id, String name) {
		STUDENT_ID = id;
		STUDENT_NAME = name;
	}

	/**
	 * This method returns the ID of the student
	 * 
	 * @return the student's ID number
	 */
	public int getID() {
		return STUDENT_ID;
	}

	/**
	 * This method return the name of the student
	 * 
	 * @return the name of the student.
	 */
	public String getName() {
		return STUDENT_NAME;
	}

	/**
	 * This method returns the attendance of the student on a given day
	 * 
	 * @return true is student was present and false is they were absent
	 */
	public boolean getAttendance() {
		return attendance;
	}

	/**
	 * This method returns the grade the student is in.
	 * 
	 * @return the grade student is in
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * This method sets the grade of the student.
	 * 
	 * @param grade the grade number you want to set to
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}

}
