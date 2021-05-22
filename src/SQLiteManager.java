
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sqlite.SQLiteDataSource;

/**
 * This class represents the SQLite database.
 * 
 * @author Arya Khokhar
 * @version 6
 */
public class SQLiteManager {
	private SQLiteDataSource ds;
	private Connection conn;
	public static String basePath;

	private static String datePattern = "yyyy-MM-dd";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	/**
	 * This constructor creates a new database if it has not already been made. If
	 * it has been made, then it connects to it.
	 * 
	 * @param basePath the string path to the database in resources
	 */
	public SQLiteManager(String basePath) {
		try {
			ds = new SQLiteDataSource();
			ds.setUrl("jdbc:sqlite:" + basePath + File.separator + "sqlite.db");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This method is for client classes to access the Connection field.
	 * 
	 * @return the connection field
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * This method creates the four different tables to represent Attendance,
	 * Classrooms, Students, and Students in Class. It also adds the sample database
	 * to it.
	 */
	public void initTables() {
		conn = getConnection();

		String classRoomQuery = "CREATE TABLE IF NOT EXISTS classroom ( " + "ID INTEGER PRIMARY KEY, "
				+ "TEACHER TEXT NOT NULL, COURSE TEXT NOT NULL)";

		String studentQuery = "CREATE TABLE IF NOT EXISTS student ( " + "ID INTEGER PRIMARY KEY, "
				+ "NAME TEXT NOT NULL, GRADE INTEGER NOT NULL)";

		String studentClassesQuery = "CREATE TABLE IF NOT EXISTS student_classes ( " + "STUDENT_ID INTEGER, "
				+ "CLASS_ID INTEGER, PRIMARY KEY (STUDENT_ID, CLASS_ID))";

		String attendenceQuery = "CREATE TABLE IF NOT EXISTS attendence ( "
				+ "STUDENT_ID INTEGER, CLASS_ID INTEGER, ATTENDENCE_DATE DATE NOT NULL, "
				+ "PRIMARY KEY (STUDENT_ID, CLASS_ID, ATTENDENCE_DATE))";

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(classRoomQuery);
			stmt.executeUpdate(studentQuery);
			stmt.executeUpdate(studentClassesQuery);
			stmt.executeUpdate(attendenceQuery);

			addSampleDatabase();

			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method creates a sample database for the means of testing and running
	 * the jar so users can experiment at first. This method can easily be removed
	 * and changed so that a user can also start with nothing.
	 * 
	 * @throws SQLException if SQL syntax error
	 */
	private void addSampleDatabase() throws SQLException {

		Statement stmt = conn.createStatement();

		ResultSet result = stmt.executeQuery("select * from student");

		if (result.next()) {
			result.close();
			return;
		}

		stmt.executeUpdate("insert into student (id, name, grade) VALUES "
				+ " (001, 'Ariana Grande', 12), (002, 'Xiao Zhan', 11), (003, 'Tom Holland', 10), "
				+ " (004, 'Zendaya Coleman', 11)");

		stmt.executeUpdate("insert into classroom (id, teacher, course) VALUES "
				+ " (2350, 'Mr. Shelby', 'AP Computer Science'), (2420, 'Mrs Westgate', 'Pre-Calculus Honors'), "
				+ " (2560, 'Mr. Cripe', 'AP Drawing'), (2570, 'Ms. Lloyd', 'Drama'), (2270, 'Mr. Burn', 'Music')");

		stmt.executeUpdate("insert into student_classes (student_id, class_id) values "
				+ "(001, 2270), (001, 2350), (002, 2570), (002, 2270), (003, 2570), "
				+ "(003, 2420), (004, 2560), (004, 2420)");

		stmt.executeUpdate("insert into attendence (student_id, class_id, attendence_date) values "
				+ "(001, 2270, '2021-05-21'), (001, 2350, '2021-05-21'), "
				+ "(002, 2570, '2021-05-21'), (002, 2270, '2021-05-21'), "
				+ "(003, 2570, '2021-05-21'), (004, 2560, '2021-05-21')");

		stmt.close();
	}

	/**
	 * This method allows a student to be added to the database.
	 * 
	 * @param student to be added
	 */
	public void addStudent(Student student) {
		try {
			Student s = getStudent(student.getID());
			if (s != null) {
				System.out.println("Student " + student.getID() + " already exists.");
				return;
			}

			Statement stmt = conn.createStatement();

			stmt.executeUpdate("insert into student (id, name, grade)" + "values (" + student.getID() + ",'"
					+ student.getName() + "'," + student.getGrade() + ")");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns all of the students currently in the database.
	 * 
	 * @return list of all students in the database
	 */
	public List<Student> getAllStudents() {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt.executeQuery("select id, name, grade from student");
			List<Student> students = new ArrayList<Student>();
			while (result.next()) {
				int id = result.getInt(1);
				String name = result.getString(2);
				int grade = result.getInt(3);

				Student s = new Student(id, name);
				s.setGrade(grade);
				students.add(s);
			}

			stmt.close();
			return students;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method edits the given student.
	 * 
	 * @param student to be edited
	 */
	public void updateStudent(Student student) {
		try {
			Student s = getStudent(student.getID());
			if (s == null) {
				System.out.println("Student does not exists.");
				return;
			}

			Statement stmt = conn.createStatement();

			stmt.executeUpdate("update student set name = '" + student.getName() + "', grade = " + student.getGrade()
					+ " where id = " + student.getID());
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns information about a student.
	 * 
	 * @param studentId id of the student to be returned
	 * @return inputed student
	 */
	public Student getStudent(int studentId) {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt.executeQuery("select id, name, grade from student " + " where id = " + studentId);
			if (result.next()) {
				int id = result.getInt(1);
				String name = result.getString(2);
				int grade = result.getInt(3);

				Student s = new Student(id, name);
				s.setGrade(grade);
				stmt.close();
				return s;
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method deletes a student from database.
	 * 
	 * @param id student ID to be deleted
	 * @return true if student was deleted and false otherwise
	 */
	public boolean deleteStudent(int id) {
		try {
			Statement stmt = conn.createStatement();

			Student student = getStudent(id);
			if (student == null) {
				return false;
			}
			stmt.executeUpdate("delete from student where id = " + id);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This method returns all classes in the school.
	 * 
	 * @return list of all classrooms
	 */
	public List<Classroom> getAllClassrooms() {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt.executeQuery("select id, teacher, course from classroom");
			List<Classroom> classList = new ArrayList<Classroom>();
			while (result.next()) {
				int id = result.getInt(1);
				String teacher = result.getString(2);
				String course = result.getString(3);

				Classroom c = new Classroom(id, course);
				c.setTeacherName(teacher);
				classList.add(c);
			}

			stmt.close();
			return classList;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method adds a classroom object to the database.
	 * 
	 * @param classroom to be added
	 */
	public void addClassroom(Classroom classroom) {
		try {
			Classroom c = getClassroom(classroom.getId());
			if (c != null) {
				System.out.println("Classroom " + c.getId() + " already exists.");
				return;
			}

			Statement stmt = conn.createStatement();

			stmt.executeUpdate("insert into classroom (id, course, teacher)" + "values (" + classroom.getId() + ",'"
					+ classroom.getCourseName() + "','" + classroom.getTeacherName() + "')");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns all of the students in a particular class.
	 * 
	 * @param classroomId to get students from
	 * @return list of students in the class
	 */
	public List<Integer> getStudentsInClassroom(int classroomId) {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt
					.executeQuery("select student_id from student_classes where " + " class_id = " + classroomId);
			List<Integer> studentIds = new ArrayList<Integer>();
			while (result.next()) {
				int id = result.getInt(1);
				studentIds.add(id);
			}

			stmt.close();
			return studentIds;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method deletes a classroom in the database.
	 * 
	 * @param classRoomId to be deleted
	 * @return true if classroom was deleted, false otherwise
	 */
	public boolean deleteClassroom(int classRoomId) {
		try {
			Statement stmt = conn.createStatement();

			Classroom classroom = getClassroom(classRoomId);
			if (classroom == null) {
				return false;
			}
			stmt.executeUpdate("delete from classroom where id = " + classRoomId);
			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This method returns the classroom details about a particular class.
	 * 
	 * @param classRoomId Id of classroom to be returned
	 * @return info about class
	 */
	public Classroom getClassroom(int classRoomId) {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt.executeQuery("select id, teacher, course from classroom where id = " + classRoomId);
			;
			if (result.next()) {
				int id = result.getInt(1);
				String teacher = result.getString(2);
				String course = result.getString(3);

				Classroom c = new Classroom(id, course);
				c.setTeacherName(teacher);
				stmt.close();
				return c;
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method updates the information in a classroom based on the users
	 * changes.
	 * 
	 * @param classroom to be updated
	 */
	public void updateClassroom(Classroom classroom) {
		try {
			Classroom c = getClassroom(classroom.getId());
			if (c == null) {
				System.out.println("Classroom does not exists.");
				return;
			}

			Statement stmt = conn.createStatement();

			stmt.executeUpdate("update classroom set course = '" + classroom.getCourseName() + "', teacher = '"
					+ classroom.getTeacherName() + "' where id = " + classroom.getId());
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns the students who were present in a classroom on a
	 * particular date.
	 * 
	 * @param classroomId to find attendance from
	 * @param date      to get attendance from
	 * @return list of students who were present
	 */
	public List<Integer> getStudentAttendence(int classroomId, Date date) {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt.executeQuery("select student_id from attendence where " + " class_id = "
					+ classroomId + " and attendence_date = '" + dateFormatter.format(date) + "'");
			;
			List<Integer> studentIds = new ArrayList<Integer>();
			while (result.next()) {
				int id = result.getInt(1);
				studentIds.add(id);
			}

			stmt.close();
			return studentIds;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method saves the student's attendence.
	 * 
	 * @param classroomId Id of the classroom
	 * @param studentId of student to marked as absent/present
	 * @param date Attendance date
	 * @param present true if student is present false if absent
	 */
	public void saveStudentAttendence(int classroomId, int studentId, Date date, boolean present) {
		try {
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("delete from attendence where student_id = " + studentId + " and class_id = "
					+ classroomId + " and attendence_date = '" + dateFormatter.format(date) + "'");

			if (present == false) {
				stmt.close();
				return;
			}

			stmt.executeUpdate("insert into attendence (student_id, class_id, attendence_date) " + "values ("
					+ studentId + "," + classroomId + ",'" + dateFormatter.format(date) + "')");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method checks if a student is taking a class.
	 * 
	 * @param studentId   student id to be checked if he/she is taking a class
	 * @param classroomId classroom id which student is taking
	 * @return boolean indicating if the student is taking a class
	 */
	private boolean checkStudentClassroom(int studentId, int classroomId) {
		try {
			Statement stmt = conn.createStatement();

			ResultSet result = stmt.executeQuery("select student_id from student_classes where " + "student_id = "
					+ studentId + " and class_id = " + classroomId);
			if (result.next()) {
				stmt.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * This method adds a given student ID to a given classroom ID.
	 * 
	 * @param studentId   student id to be added to a classroom
	 * @param classroomId classroom id to which a student is to be added
	 * @return boolean indicating if the student was added to the class
	 */
	public boolean addStudentToClassroom(int studentId, int classroomId) {
		try {

			boolean found = checkStudentClassroom(studentId, classroomId);
			if (found) {
				return false;
			}

			Statement stmt = conn.createStatement();

			stmt.executeUpdate("insert into student_classes(student_id, class_id) values (" + studentId + ", "
					+ classroomId + ")");

			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * This method removes a given student id from a given classroom ID.
	 * 
	 * @param studentId student ID to be deleted from a classroom
	 * @param classroomId classroom ID from which a student is to be removed
	 * @return true if the student was removed from the class and false otherwise
	 */
	public boolean deleteStudentFromClassroom(int studentId, int classroomId) {
		try {

			boolean found = checkStudentClassroom(studentId, classroomId);
			if (!found) {
				return false;
			}

			Statement stmt = conn.createStatement();

			stmt.executeUpdate(
					"delete from student_classes where student_id = " + studentId + " and class_id = " + classroomId);
			stmt.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}
