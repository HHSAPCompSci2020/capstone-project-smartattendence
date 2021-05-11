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
		s = new ArrayList<Classroom>();
	}

	/**
	 * @pre cannot have two classes with the same name
	 * @param ArrayList<classroom> input, a input list of classes.
	 */
	public School(ArrayList<Classroom> input) {
		s = input;
	}

	/**
	 * This method adds a classroom to the school
	 * 
	 * @param Classroom classroom, the classroom you want to add
	 * @post Changes ArrayList field
	 */
	public void add(Classroom classroom) {
		s.add(classroom);

	}

	/**
	 * This method removes a classroom from the school
	 * 
	 * @param Classroom classroom, the classroom you want to remove
	 * @post Changes ArrayList field
	 */
	public void remove(Classroom classroom) {
		s.remove(classroom);
	}

}
