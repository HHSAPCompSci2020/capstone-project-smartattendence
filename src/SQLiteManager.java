
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
		Connection conn = getConnection();
		
		String classRoomQuery = "CREATE TABLE IF NOT EXISTS classroom ( " +
                "ID INTEGER PRIMARY KEY, " +
                "TEACHER TEXT NOT NULL, SUBJECT TEXT NOT NULL)";

		String studentQuery = "CREATE TABLE IF NOT EXISTS student ( " +
                "ID INTEGER PRIMARY KEY, " +
                "NAME TEXT NOT NULL, GRADE INTEGER NOT NULL, NICKNAME TEXT NOT NULL)";

		String studentClassesQuery = "CREATE TABLE IF NOT EXISTS student_classes ( " +
                "STUDENT_ID INTEGER PRIMARY KEY, " +
                "CLASS_ID INTEGER)";
		
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(classRoomQuery);
			stmt.executeUpdate(studentQuery);
			stmt.executeUpdate(studentClassesQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void addStudent(Student student) {		
	}

	public List<Student> getAllStudents() {
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
		return null;
	}

	public void updateClassroom(Classroom newClassroom) {
		// TODO Auto-generated method stub
		
	}
}
