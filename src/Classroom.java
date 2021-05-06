import java.util.ArrayList;

/**
 * This class represents the classroom and contians an arraylist 
 * of students.
 * @author Arya Khokhar
 *
 */
public class Classroom {

	ArrayList<Student> classroom;
	
	
	public Classroom() {
		classroom = null;
	}
	
	public Classroom(ArrayList<Student> c) {
		classroom = c;
	}
}
