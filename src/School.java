import java.util.ArrayList;

/**
 * @author Xinyu Zhao This is the School class, where it will contain Classroom
 */
public class School {

	ArrayList<Classroom> s;

	public School() {

	}

	// adds a Classroom
	public void add(Classroom classroom) {
		s.add(classroom);

	}

	// removes a Classroom
	public void remove(Classroom classroom) {
		s.remove(classroom);
	}

}
