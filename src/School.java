import java.util.ArrayList;

/**
 * This is the School class, where it will contain Classroom
 * @author Xinyu Zhao 
 * @version 2
 */
public class School {

	ArrayList<Classroom> s;

	/**
	 * creates a new School
	 * @pre cannot have two classes with the same name
	 */
	public School() {
		s = new ArrayList<Classroom>();
	}

	/**
	 * creates a new school
	 * @pre cannot have two classes with the same name
	 * @param input a input list of classes.
	 */
	public School(ArrayList<Classroom> input) {
		s = input;
	}

	/**
	 * This method adds a classroom to the school
	 * 
	 * @param classroom the classroom you want to add
	 * @post Changes ArrayList field
	 */
	public void add(Classroom classroom) {
		s.add(classroom);

	}

	/**
	 * This method removes a classroom from the school
	 * 
	 * @param classroom the classroom you want to remove
	 * @post Changes ArrayList field
	 */
	public void remove(Classroom classroom) {
		s.remove(classroom);
	}

}
