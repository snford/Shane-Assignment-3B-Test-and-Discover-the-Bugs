package edu.loyola.sghersick.A3_BugHunt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin utility to manage a roster and evaluate requirement completion.
 */
public class Admin {

    private final ArrayList<Student> students;
    private final double hoursPerWeek;
    private final double maxDayHours;

    public Admin(ArrayList<Student> students, double hoursPerWeek, double maxDayHours) {
        this.students = students == null ? new ArrayList<>() : students;
        this.hoursPerWeek = hoursPerWeek;
        this.maxDayHours = maxDayHours;
    }

    /** Returns only students who did NOT meet the weekly requirement on 'deadline'. */
    public HashMap<Student, double[]> getSlackers(LocalDate deadline){
        HashMap<Student, double[]> out = new HashMap<>();
        for (Student s : students) {
            double[] res = s.calculateWeekHours(deadline, hoursPerWeek, maxDayHours);
            if (res[0] + 1e-9 < hoursPerWeek) { // epsilon for floating point
                out.put(s, res);
            }
        }
        return out;
    }

    /** Adds a session to the student with the given id. */
    public void addSession(int studentId, Session s) {
        Student target = getStudentById(studentId);
        if (target == null) {
            throw new IllegalArgumentException("No student with id: " + studentId);
        }
        target.addSession(s);
    }

    private Student getStudentById(int studentId) {
        for (Student s : students) {
            if (s.isId(studentId)) return s;
        }
        return null;
    }

    public static void main(String[] args) {
        final double REQUIRED_HOURS = 8.0;
        final double MAX_DAY_HOURS = 4.0;
        final LocalDate deadline = LocalDate.of(2025, 10, 9);

        final int[] studentID = {202717, 219753, 263254};
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(studentID[0], "Sam Hersick", SportsTeam.SWIMMING));
        students.add(new Student(studentID[1], "Tom McCarthy", SportsTeam.SOCCER));
        students.add(new Student(studentID[2], "John Smith", SportsTeam.TENNIS));

        Admin admin = new Admin(students, REQUIRED_HOURS, MAX_DAY_HOURS);

        ArrayList<Session> sessions = new ArrayList<>();
        sessions.add(new Session(LocalDateTime.of(2025,10,3,1,0), LocalDateTime.of(2025,10,3,10,0))); // 9h -> cap 4
        sessions.add(new Session(LocalDateTime.of(2025,10,4,1,0), LocalDateTime.of(2025,10,4,2,0)));  // 1h
        sessions.add(new Session(LocalDateTime.of(2025,10,7,1,0), LocalDateTime.of(2025,10,7,3,0)));  // 2h

        for (int id : studentID) {
            for (Session s : sessions) {
                if (id != studentID[0]) admin.addSession(id, s);
            }
        }

        HashMap<Student, double[]> slackers = admin.getSlackers(deadline);
        System.out.println(deadline + " (" + slackers.size() + "):");
        for (Map.Entry<Student, double[]> set : slackers.entrySet()) {
            System.out.println(set.getKey()+" - hours complete: "+set.getValue()[0]+" - hours banked: "+set.getValue()[1]);
        }
    }
}