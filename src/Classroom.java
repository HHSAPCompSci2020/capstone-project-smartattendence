import java.util.ArrayList;

/**
 * This class represents the classroom and contians an arraylist of students.
 * @author Xinyu zhao
 * @version 3
 *
 */

public class Classroom {

	ArrayList<Student> classroom;
	private int id;
	private String courseName;
	private String teacherName;
	
	/**
	 * creates a new classroom
	 * @pre cannot have two students with the same name!!
	 */
	public Classroom() {
		classroom = null;
	}

	/**
	 * creates a new classroom
	 * @pre cannot have two students with the same name!!
	 * @param String name: the name of the class
	 */
	public Classroom(int id, String courseName) {
		this.id = id;
		this.courseName = courseName;
	}

	/**
	 * This method adds a new student to the class
	 *
	 * @param s Takes in a Student object
	 * @post ArrayList classroom has a new student
	 */
	public void addStudent(Student s) {
		classroom.add(s);
	}

	/**
	 * This method removes a student from the class
	 *
	 * @param s Takes in a Student object
	 * @post ArrayList classroom has one less student
	 */
	public void removeStudent(Student s) {
		classroom.remove(s);

	}
/**
 * the method called to get the name of the course
 * @return a String of the courseName
 */
	public String getCourseName() {
		return courseName;
	}
	/**
	 * sets the courseName to what you want it to be
	 * @param String courseName: the course name you want to change
	 */
	public void setCouseName(String courseName) {
		this.courseName = courseName;
	}
	/**
	 * return the name of the course teacher
	 * @return a String of the teacherName
	 */
	public String getTeacherName() {
		return teacherName;
	}
	/**
	 * sets the teacherName to what you want it to be
	 * @param String teacherName: the course name you want to change
	 */
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
/**
 * return the id of the course.
 * @return int id, the id of the course
 */
	public int getId() {
		return id;
	}
}
