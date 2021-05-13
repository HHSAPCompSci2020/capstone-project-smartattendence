import java.util.ArrayList;

/**
 * This class represents the classroom and contians an arraylist of students.
 * 
 * @author Arya Khokhar, xinyu zhao
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

	public String getCourseName() {
		return courseName;
	}

	public void setCouseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public int getId() {
		return id;
	}
}
