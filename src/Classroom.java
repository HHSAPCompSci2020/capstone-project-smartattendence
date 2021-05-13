import java.util.ArrayList;

/**
 * This class represents the classroom and contians an arraylist of students.
 * 
 * @author Arya Khokhar, xinyu zhao
 *
 */

public class Classroom {

	ArrayList<Student> classroom;
<<<<<<< HEAD
	private int id;
	private String courseName;
	private String teacherName;


	/**
	 * @pre cannot have two classrooms with the same name!!
=======
	String name;

	/**
	 * @pre cannot have two students with the same name!!
>>>>>>> 4a81b7bfc0c39f8060a08383c1f236eb4b9669fd
	 */
	public Classroom() {
		classroom = null;
	}

	/**
<<<<<<< HEAD
	 * @pre cannot have two classrooms with the same name!!
	 */
	public Classroom(int id, String courseName) {
		this.id = id;
		this.courseName = courseName;
=======
	 * @pre cannot have two students with the same name!!
	 */
	public Classroom(String name) {
		this.name = name;
>>>>>>> 4a81b7bfc0c39f8060a08383c1f236eb4b9669fd
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

<<<<<<< HEAD
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
=======
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPeriod() {
		// TODO Auto-generated method stub
		return -1;
	}

	public String getTeacher() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPeriod(int period) {
		// TODO Auto-generated method stub

	}

	public void setTeacher(String teacher) {
		// TODO Auto-generated method stub

>>>>>>> 4a81b7bfc0c39f8060a08383c1f236eb4b9669fd
	}
}
