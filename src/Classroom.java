import java.util.ArrayList;

/**
 * This class represents the classroom and contians an arraylist of students.
 * 
 * @author Arya Khokhar, xinyu zhao
 *
 */

public class Classroom {

	ArrayList<Student> classroom;
	String name;

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
	public Classroom(String name) {
		this.name = name;
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

	}
}
