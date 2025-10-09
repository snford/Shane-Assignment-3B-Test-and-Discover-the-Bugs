package edu.loyola.sghersick.A3_BugHunt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Admin {
	// Attributes
	private ArrayList<Student> students;
	private double hoursPerWeek;
	private double maxDayHours;
	
	// Constructors
	public Admin(ArrayList<Student> students, double hoursPerWeek, double maxDayHours) {
		this.students = (ArrayList<Student>) students.clone(); // shallow copy
		this.hoursPerWeek = hoursPerWeek;
		this.maxDayHours = maxDayHours;
	}
	
	/**
	 * Get Map of each slacker, and they're hour requirements for deadline
	 * @param deadline
	 * @return
	 */
	public HashMap<Student, double[]> getSlackers(LocalDate deadline){
		HashMap<Student, double[]> slackers = new HashMap<>();
		
		for(Student student: students) {
			double[] studentHours = student.calculateWeekHours(deadline, maxDayHours, hoursPerWeek);
			
			if(studentHours[0] < hoursPerWeek) {
				slackers.put(student, studentHours);
			}
		}
		
		return slackers;
	}
	
	/**
	 * Admin adds session to user by id (if user does not exist, throw exception)
	 * @param student's id
	 * @param session to add
	 */
	public void addSession(int studentId, Session s) {
		Student student = getStudentById(studentId);
		if(s == null) {
			students.get(0).addSession(s);
		}
		
		student.addSession(s);
	}
	
	/**
	 * Return a student given their id
	 * @param studentId
	 * @return Student object when found, else null
	 */
	private Student getStudentById(int studentId) {
		for(Student s: students) {
			if(s.isId(studentId) == true) return s;
		}
		
		return null;
	}
}

// Demonstrate running logic
class Demo{
	// Valid Test Values
	static final double REQUIRED_HOURS = 8.0;
	static final double MAX_DAY_HOURS = 4.0;
	static final LocalDate deadline = LocalDate.of(2025, 10, 9);

	static final int studentID[] = {202717, 219753, 263254};
	static final ArrayList<Student> students = new ArrayList<Student>(Arrays.asList(
			new Student(studentID[0], "Sam Hersick", SportsTeam.SWIMMING),
			new Student(studentID[1], "Tom McCarthy", SportsTeam.SOCCER),
			new Student(studentID[2], "John Smith", SportsTeam.TENNIS)));
	
	public static void main(String[] args) {
		// 1. Admin creates list of students
		Admin admin = new Admin(students, REQUIRED_HOURS, MAX_DAY_HOURS);
		
		// 2. Admin add sessions for students (time in study)
		ArrayList<Session> sessions = new ArrayList<>(Arrays.asList(
				new Session(LocalDateTime.of(2025, 10, 3, 1, 00), LocalDateTime.of(2025, 10, 3, 10, 00)),
				new Session(LocalDateTime.of(2025, 10, 4, 1, 00), LocalDateTime.of(2025, 10, 4, 2, 00)),
				new Session(LocalDateTime.of(2025, 10, 7, 1, 00), LocalDateTime.of(2025, 10, 7, 3, 00))));
		
		// 3. Admin can add student sessions by id
		for(int id : studentID) {
			for(Session s: sessions) {
				if(id != studentID[0]) admin.addSession(id, s); // Give 2 students, all 3 sessions
			}
		}
		
		// 4. Admin can check who HAS NOT met requirement
		HashMap<Student, double[]> slackers = admin.getSlackers(deadline);
		
		// 5. Print out slackers
		System.out.println(deadline+" ("+slackers.size()+"):");
		for(Map.Entry<Student, double[]> set : slackers.entrySet()) {
			System.out.println(set.getKey()+" - hours complete: "+set.getValue()[0]+" - hours banked: "+set.getValue()[1]);
		}
		
		// You can see here all 3 students got flagged, but "Sam Hersick" completed less hours than the other students
	}
}

