
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
 * This class represents the database.
 * @author Arya Khokhar
 * @version 4
 */
public class SQLiteManager {
	private SQLiteDataSource ds;
	private Connection conn;
	public static String basePath;

	static String datePattern = "yyyy-MM-dd";
	static SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
	 
	SQLiteManager(String basePath) {
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

		System.out.println("Opened database");
	}
	
	public Connection getConnection() {
		return conn;
	}

	public void initTables() {
		conn = getConnection();
		
		String classRoomQuery = "CREATE TABLE IF NOT EXISTS classroom ( " +
                "ID INTEGER PRIMARY KEY, " +
                "TEACHER TEXT NOT NULL, COURSE TEXT NOT NULL)";

		String studentQuery = "CREATE TABLE IF NOT EXISTS student ( " +
                "ID INTEGER PRIMARY KEY, " +
                "NAME TEXT NOT NULL, GRADE INTEGER NOT NULL)";

		String studentClassesQuery = "CREATE TABLE IF NOT EXISTS student_classes ( " +
                "STUDENT_ID INTEGER, " +
                "CLASS_ID INTEGER, PRIMARY KEY (STUDENT_ID, CLASS_ID))";
		
		String attendenceQuery = "CREATE TABLE IF NOT EXISTS attendence ( " +
                "STUDENT_ID INTEGER, CLASS_ID INTEGER, ATTENDENCE_DATE DATE NOT NULL, "
				+ "PRIMARY KEY (STUDENT_ID, CLASS_ID, ATTENDENCE_DATE))";

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(classRoomQuery);
			stmt.executeUpdate(studentQuery);
			stmt.executeUpdate(studentClassesQuery);
			stmt.executeUpdate(attendenceQuery);
			
			addSampleDatabase(stmt);
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void addSampleDatabase(Statement stmt) throws SQLException {
		
		ResultSet result = stmt.executeQuery("select * from student");
		
		if (result.next()) {
			result.close();
			return;
		}
		
		stmt.executeUpdate("insert into student (id, name, grade) VALUES "
				+ " (123, 'Arya', 10), (456, 'Xinyu', 11), (678, 'Jones', 10)");
		
		stmt.executeUpdate("insert into classroom (id, teacher, course) VALUES "
				+ " (2350, 'Mr. Shelby', 'AP Computer Science'), (2420, 'Mrs Westgate', 'Pre-Calculus Honors')");

		stmt.executeUpdate("insert into student_classes (student_id, class_id) values "
				+ "(123, 2350), (456, 2350), (678, 2350), (123, 2420)");
		
		stmt.executeUpdate("insert into attendence (student_id, class_id, attendence_date) values "
				+ "(123, 2350, '2021-05-03'), (123, 2350, '2021-05-06'), "
				+ "(456, 2350, '2021-05-03'), (456, 2350, '2021-05-06'), "
				+ "(678, 2350, '2021-05-06')");
	}
	
	/**
	 * This method allows a student to be added to the database
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
			
			stmt.executeUpdate("insert into student (id, name, grade)"
					+ "values (" + student.getID() + ",'" + student.getName() + "',"
					+ student.getGrade() + ")");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
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
	 * Edits the given student
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
			
			stmt.executeUpdate("update student set name = '" + student.getName()
				+ "', grade = " + student.getGrade()
				+ " where id = " + student.getID());
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns information about a student 
	 * @param studentId id of the student to be returned
	 * @return inputed student
	 */
	public Student getStudent(int studentId) {
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet result = stmt.executeQuery("select id, name, grade from student "
					+ " where id = " + studentId);
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
	 * Deletes a student from database
	 * @param id student id to be deleted
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
	 * Returns all classes in the school
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
			
			stmt.executeUpdate("insert into classroom (id, course, teacher)"
					+ "values (" + classroom.getId() + ",'" + classroom.getCourseName() + "','"
					+ classroom.getTeacherName() + "')");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param classroom to get students 
	 * @return list of students in the class
	 */
	public List<Integer> getStudentsInClassroom(int classroomId) {
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet result = stmt.executeQuery("select student_id from student_classes where "
					+" class_id = " + classroomId);
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
	 * @param classroomId to be deleted
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
	 * 
	 * @param classroomId Id of classroom to be returned
	 * @return info about class
	 */
	public Classroom getClassroom(int classRoomId) {
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet result = stmt.executeQuery(
					"select id, teacher, course from classroom where id = " + classRoomId);;
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
	 * @param newClassroom to be updated
	 */
	public void updateClassroom(Classroom classroom) {
		try {
			Classroom c = getClassroom(classroom.getId());
			if (c == null) {
				System.out.println("Classroom does not exists.");
				return;
			}
			
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate("update classroom set course = '" + classroom.getCourseName()
				+ "', teacher = '" + classroom.getTeacherName()
				+ "' where id = " + classroom.getId());
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param classroom to find attendance from
	 * @param date to get attendance from
	 * @return list of students and whether they were absent or not
	 */
	public List<Integer> getStudentAttendence(int classroomId, Date date) {
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet result = stmt.executeQuery("select student_id from attendence where "
					+" class_id = " + classroomId + " and attendence_date = '"
					+ dateFormatter.format(date) + "'");;
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
	 * @param classroomId Id of the classroom
	 * @param date Attendence date
	 * @return studentId Id of student to marked as absent/present
	 * @return present boolean indicating if student is present or absent
	 */
	public void saveStudentAttendence(int classroomId, int studentId, Date date, boolean present) {
		try {
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("delete from attendence where student_id = " + studentId
					+ " and class_id = " + classroomId + " and attendence_date = '"
					+ dateFormatter.format(date) + "'");
			
			if (present == false) {
				stmt.close();
				return;
			}

			stmt.executeUpdate("insert into attendence (student_id, class_id, attendence_date) "
					+ "values (" + studentId + "," + classroomId + ",'" 
					+ dateFormatter.format(date) + "')");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if a student is taking a class.
	 * @param studentId student id to be checked if he/she is taking a class
	 * @param classroomId classroom id which student is taking
	 * @return boolean indicating if the student is taking a class
	 */
	public boolean checkStudentClassroom(int studentId, int classroomId) {
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
	 * Adds a given student id to a given classroom id.
	 * @param studentId student id to be added to a classroom
	 * @param classroomId classroom id to which a student is to be added
	 * @return boolean indicating if the student was added to the class
	 */
	public boolean addStudentToClassroom(int studentId, int classroomId) {
		try {
			
			boolean found = checkStudentClassroom(studentId,  classroomId);
			if (found) {
				return false;
			}
			
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate("insert into student_classes(student_id, class_id) values ("
					+ studentId + ", " + classroomId + ")");

			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Removes a given student id from a given classroom id.
	 * @param studentId student id to be deleted from a classroom
	 * @param classroomId classroom id from which a student is to be removed
	 * @return boolean indicating if the student was removed from the class
	 */
	public boolean deleteStudentFromClassroom(int studentId, int classroomId) {
		try {
			
			boolean found = checkStudentClassroom(studentId,  classroomId);
			if (!found) {
				return false;
			}
			
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate("delete from student_classes where student_id = " + studentId
					+ " and class_id = " + classroomId);
			stmt.close();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
