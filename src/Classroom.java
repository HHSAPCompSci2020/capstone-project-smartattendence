import java.util.ArrayList;

/**
 * This class represents the classroom and contians an arraylist of students.
 * 
 * @author Arya Khokhar, xinyu zhao
 *
 */

public class Classroom {

	ArrayList<StudentPanel> classroom;

	/**
	 * @pre cannot have two students with the same name!!
	 */
	public Classroom() {
		classroom = null;
	}

	/**
	 * @pre cannot have two students with the same name!!
	 */
	public Classroom(ArrayList<StudentPanel> c) {
		classroom = c;
	}

	/**
	 * This method adds a new student to the class
	 * 
	 * @param s Takes in a Student object
	 * @post ArrayList classroom has a new student
	 */
	public void addStudent(StudentPanel s) {
		classroom.add(s);
	}

	/**
	 * This method removes a student from the class
	 * 
	 * @param s Takes in a Student object
	 * @post ArrayList classroom has one less student
	 */
	public void removeStudent(StudentPanel s) {
		classroom.remove(s);

	}
}
