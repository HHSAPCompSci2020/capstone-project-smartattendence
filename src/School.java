import java.util.ArrayList;

/**
 * @author Xinyu Zhao This is the School class, where it will contain Classroom
 */
public class School {

	ArrayList<Classroom> s;

	/**
	 * @pre cannot have two classes with the same name
	 */
	public School() {

	}

	/**
	 * This method adds a classroom to the school
	 * 
	 * @param classroom
	 * @post Changes arraylist field
	 */
	public void add(Classroom classroom) {
		s.add(classroom);

	}

	/**
	 * This method removes a classroom from the school
	 * 
	 * @param classroom
	 * @post Changes arraylist field
	 */
	public void remove(Classroom classroom) {
		s.remove(classroom);
	}

}
