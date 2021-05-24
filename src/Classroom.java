import java.util.ArrayList;

/**
 * This class represents the classroom and contains an ArrayList of Students.
 * 
 * @author Xinyu Zhao
 * @version 4
 */
public class Classroom {

	private ArrayList<Student> classroom;
	private int id;
	private String courseName;
	private String teacherName;
	
	/**
	 * This default constructor creates a new empty classroom.
	 */
	public Classroom() {
		classroom = null;
	}

	/**
	 * This constructor creates a new classroom with the given ID and course name.
	 * 
	 * @param courseName the name of the class
	 * @param id the ID/course number of the class
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
	 * This method adds a new student to the class.
	 *
	 * @param s takes in a Student object
	 * @post ArrayList classroom has a new student
	 */
	public void removeStudent(Student s) {
		classroom.remove(s);

	}
	
	/**
	 * The method is called from client classes to get the name of the course.
	 * 
	 * @return a string of the courseName
	 */
	public String getCourseName() {
		return courseName;
	}
	/**
	 * 
	 * This method sets the courseName field to what the user wants it to be.
	 * 
	 * @param courseName the course name you want to change
	 */
	public void setCouseName(String courseName) {
		this.courseName = courseName;
	}
	
	/**
	 * This method is called from client classes and returns the name of the course teacher.
	 * 
	 * @return a String of the teacherName
	 */
	public String getTeacherName() {
		return teacherName;
	}
	
	/**
	 * This method sets the teacherName to what the user wants it to be.
	 * 
	 * @param teacherName the course name you want to change
	 */
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	
	/**
	 * This method is called from client classes and returns the id of the course.
	 * 
	 * @return id the id of the course
	 */
	public int getId() {
		return id;
	}
}
