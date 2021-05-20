import java.awt.Image;

/**
 * This class represent a Student.
 * @author xinyu zhao
 * @version 3
 *
 */
public class Student {

	private final int STUDENT_ID;
	private final String STUDENT_NAME;
	private boolean attendance = false;
	private int grade;
/**
 * creates a Student, defult set the id to 0 and name to ""
 */
	public Student() {
		STUDENT_ID = 0;
		STUDENT_NAME = "";
	}
/**
 * creates a student with id and name
 * @param id the student's Id number
 * @param name the student's name
 */
	public Student(int id, String name) {
		STUDENT_ID = id;
		STUDENT_NAME = name;
	}

	/**
	 * This method returns the ID of the student
	 * @return the student's id number
	 */
	public int getID() {
		return STUDENT_ID;
	}

	/**
	 * This method return the name of the student
	 * @return the name of the student. 
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

/**
 * this method returns the grade the student is in.
 * @return and int of the grade student is in
 */
	public int getGrade() {
		return grade;
	}
/**
 * this method sets the grade of the student.
 * @param grade the grade number you want to set to
 */

	public void setGrade(int grade) {
		this.grade = grade;
	}


}
