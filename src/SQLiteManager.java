
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sqlite.SQLiteDataSource;

/**
 * This class represents the database.
 * @author Arya Khokhar
 * @version 3
 */
public class SQLiteManager {
	private SQLiteDataSource ds;
	private Connection conn;
	public static String basePath;

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
				+ "PRESENT INTEGER, PRIMARY KEY (STUDENT_ID, CLASS_ID, ATTENDENCE_DATE))";

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
		
		stmt.executeUpdate("insert into attendence (student_id, class_id, attendence_date, present) values "
				+ "(123, 2350, '2021-05-03', 1), (123, 2350, '2021-05-06', 1), "
				+ "(456, 2350, '2021-05-03', 1), (456, 2350, '2021-05-06', 1), "
				+ "(678, 2350, '2021-05-03', 0), (678, 2350, '2021-05-06', 1)");
	}
	
	public void addStudent(Student student) {		
	}

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

	public void updateStudent(Student student) {
	}

	public Student getStudent(String studentName) {
		return null;
	}

	public void deleteStudent(String studentName) {		
	}

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

	public void addClassroom(Classroom classroom) {
	}

	public List<Student> getStudentsInClassroom(Classroom classroom) {
		return null;
	}

	public void deleteClassroom(String classroomName) {
	}

	public Classroom getClassroom(String classroomName) {
		try {
			Statement stmt = conn.createStatement();
			
			ResultSet result = stmt.executeQuery(
					"select id, teacher, course from classroom where course = '"
					+ classroomName + "'");;
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

	public void updateClassroom(Classroom newClassroom) {
	}
	
	public List<Student> getStudentAttendence(Classroom classroom, Date date) {
		return null;
	}
}
