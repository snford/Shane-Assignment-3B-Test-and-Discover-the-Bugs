package edu.loyola.sghersick.A3_BugHunt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Student
{
    // Attributes
    private int studentId;
    private String name;
    private SportsTeam sport;
    private ArrayList<Session> sessions = new ArrayList<Session>();

    // Constructor
    public Student(int studentId, String name, SportsTeam sport) {
        this.studentId = studentId;
        this.name = name;
        this.sport = sport;
    }

    // Check if id matches
    public boolean isId(int studentId) {
        return this.studentId == studentId; // (keep signature; ensure correct comparison)
    }

    // Add session
    public void addSession(Session s) {
        if (s != null) sessions.add(s);
    }

    /**
     * Calculate week hours:
     * returns double[]{completed, banked}
     * - Window: 7 days ending on 'deadline' (inclusive)
     * - Per-day cap applied before weekly sum
     */
    public double[] calculateWeekHours(LocalDate deadline, double weeklyRequirement, double maxPerDay) {
        LocalDate start = deadline.minusDays(6);

        // sum raw hours per day within the window
        Map<LocalDate, Double> perDay = new HashMap<>();
        for (Session s : sessions) {
            LocalDate d = s.getDate();
            if ((d.isEqual(start) || d.isAfter(start)) && (d.isEqual(deadline) || d.isBefore(deadline))) {
                perDay.merge(d, s.calcDuration(), Double::sum);
            }
        }

        // apply per-day cap before weekly aggregation
        double weeklyCapped = 0.0;
        for (Map.Entry<LocalDate, Double> e : perDay.entrySet()) {
            weeklyCapped += Math.min(maxPerDay, e.getValue());
        }

        double completed = Math.min(weeklyRequirement, weeklyCapped);
        double banked = Math.max(0.0, weeklyCapped - weeklyRequirement);
        return new double[]{ completed, banked };
    }

    // Print this student's information
    public String toString() {
        return name + " ("+studentId+") ["+sport+"]";
    }
}

