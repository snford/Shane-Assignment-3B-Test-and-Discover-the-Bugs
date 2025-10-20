package edu.loyola.sghersick.A3_BugHunt;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This class contains unit tests for:
 * - Session.java
 * - Student.java
 * - Admin.java
 *
 * Each test checks a specific behavior of the code, making sure bugs are caught early
 * and future changes don't break the program.
 */
public class Shane_AllTests {

    // ============================
    // SESSION CLASS TESTS
    // ============================

    /**
     * Checks that session duration is correctly calculated in hours
     * and that the date is extracted properly.
     */
    @Test
    @DisplayName("90 minutes -> 1.5 hours")
    void session_calcDuration_basic() {
        LocalDateTime in = LocalDateTime.of(2024, 3, 4, 13, 0);
        LocalDateTime out = LocalDateTime.of(2024, 3, 4, 14, 30);
        Session s = new Session(in, out);
        assertEquals(1.5, s.calcDuration(), 1e-6);
        assertEquals(LocalDate.of(2024, 3, 4), s.getDate());
    }

    /**
     * Checks that sessions with a checkout time before checkin are rejected.
     */
    @Test
    @DisplayName("Reject checkout before checkin")
    void session_invalidCheckout() {
        assertThrows(IllegalArgumentException.class, () ->
            new Session(LocalDateTime.of(2024,1,10,12,0), LocalDateTime.of(2024,1,10,11,59)));
    }

    /**
     * Checks that sessions crossing midnight (spanning multiple dates) are rejected.
     */
    @Test
    @DisplayName("Reject cross-midnight sessions")
    void session_crossMidnight() {
        assertThrows(IllegalArgumentException.class, () ->
            new Session(LocalDateTime.of(2024,1,10,23,30), LocalDateTime.of(2024,1,11,0,30)));
    }

    /**
     * Checks that zero-length sessions either throw an exception
     * or return a non-negative duration (depending on how Session is implemented).
     */
    @Test
    @DisplayName("Zero-length: either throw or >= 0 duration")
    void session_zeroLength() {
        LocalDateTime t = LocalDateTime.of(2024, 1, 10, 12, 0);
        try {
            Session s = new Session(t, t);
            assertTrue(s.calcDuration() >= 0);
        } catch (IllegalArgumentException ok) {}
    }

    // ============================
    // STUDENT CLASS TESTS
    // ============================

    /**
     * Checks that a student who exactly meets the weekly hour requirement
     * is credited correctly and has no banked time.
     */
    @Test
    @DisplayName("Exact requirement met, no bank")
    void student_weeklyExact() {
        Student st = new Student(101, "Test Student", SportsTeam.SOCCER);
        LocalDate deadline = LocalDate.of(2024, 3, 10);

        // Add sessions totaling 4 hours exactly
        st.addSession(new Session(LocalDateTime.of(2024,3,8,13,0), LocalDateTime.of(2024,3,8,14,0)));
        st.addSession(new Session(LocalDateTime.of(2024,3,9,16,0), LocalDateTime.of(2024,3,9,18,0)));
        st.addSession(new Session(LocalDateTime.of(2024,3,10,10,0), LocalDateTime.of(2024,3,10,11,0)));

        double[] res = st.calculateWeekHours(deadline, 4.0, 4.0);
        assertEquals(4.0, res[0], 1e-6);
        assertEquals(0.0, res[1], 1e-6);
    }

    /**
     * Checks that if a student does more than the per-day cap, only the cap counts toward
     * the requirement, and the remainder is banked.
     */
    @Test
    @DisplayName("Per-day cap then bank")
    void student_perDayCap() {
        Student st = new Student(102, "Cap Student", SportsTeam.BASKETBALL);
        LocalDate deadline = LocalDate.of(2024, 3, 10);

        // Student does 6 hours in one day; only 4 should count
        st.addSession(new Session(LocalDateTime.of(2024,3,7,9,0),  LocalDateTime.of(2024,3,7,12,0)));  // 3h
        st.addSession(new Session(LocalDateTime.of(2024,3,7,12,30), LocalDateTime.of(2024,3,7,15,30))); // 3h

        double[] res = st.calculateWeekHours(deadline, 3.0, 4.0);
        assertEquals(3.0, res[0], 1e-6); // only 3 required hours are counted
        assertEquals(1.0, res[1], 1e-6); // 1 hour extra is banked
    }

    /**
     * Checks that only sessions inside the 7-day window are counted toward the weekly total.
     */
    @Test
    @DisplayName("Only sessions within 7-day inclusive window count")
    void student_excludeOutsideWindow() {
        Student st = new Student(103, "Window Student", SportsTeam.TENNIS);
        LocalDate deadline = LocalDate.of(2024, 3, 10);

        // 2 hours inside window, 1 hour inside, and 1 hour outside
        st.addSession(new Session(LocalDateTime.of(2024,3,5,10,0), LocalDateTime.of(2024,3,5,12,0))); // 2
        st.addSession(new Session(LocalDateTime.of(2024,3,10,10,0), LocalDateTime.of(2024,3,10,11,0))); // 1
        st.addSession(new Session(LocalDateTime.of(2024,3,3,12,0), LocalDateTime.of(2024,3,3,13,0))); // outside

        double[] res = st.calculateWeekHours(deadline, 10.0, 8.0);
        assertEquals(3.0, res[0], 1e-6);
        assertEquals(0.0, res[1], 1e-6);
    }

    // ============================
    // ADMIN CLASS TESTS
    // ============================

    /**
     * Helper method to quickly create a student object.
     */
    private static Student mk(int id, String name, SportsTeam t) {
        return new Student(id, name, t);
    }

    /**
     * Checks that only students who have NOT met the required hours
     * are returned as "slackers" by getSlackers().
     */
    @Test
    @DisplayName("Slackers are only those below requirement")
    void admin_slackersOnly() {
        Student a = mk(1, "Alice", SportsTeam.GOLF);
        Student b = mk(2, "Bob", SportsTeam.SOCCER);
        Student c = mk(3, "Cyd", SportsTeam.LACROSSE);
        LocalDate d = LocalDate.of(2024, 3, 10);

        // A meets requirement (3h)
        a.addSession(new Session(LocalDateTime.of(2024,3,9,10,0), LocalDateTime.of(2024,3,9,13,0)));
        // B does 2.5h - below requirement
        b.addSession(new Session(LocalDateTime.of(2024,3,8,10,0), LocalDateTime.of(2024,3,8,12,0)));
        b.addSession(new Session(LocalDateTime.of(2024,3,10,12,0), LocalDateTime.of(2024,3,10,12,30)));
        // C exceeds requirement
        c.addSession(new Session(LocalDateTime.of(2024,3,7,9,0), LocalDateTime.of(2024,3,7,11,0)));
        c.addSession(new Session(LocalDateTime.of(2024,3,10,9,0), LocalDateTime.of(2024,3,10,11,30)));

        ArrayList<Student> roster = new ArrayList<>();
        roster.add(a); roster.add(b); roster.add(c);

        Admin admin = new Admin(roster, 3.0, 4.0);
        HashMap<Student,double[]> slackers = admin.getSlackers(d);

        // Only B should be flagged as a slacker
        assertEquals(1, slackers.size());
        assertTrue(slackers.containsKey(b));
        assertFalse(slackers.containsKey(a));
        assertFalse(slackers.containsKey(c));
    }

    /**
     * Checks that when a session is added by student ID,
     * it is properly routed to the correct student object.
     */
    @Test
    @DisplayName("addSession routes to correct student by id")
    void admin_addSessionById() {
        Student a = mk(7, "Seven", SportsTeam.SWIMMING);
        Student b = mk(9, "Nine", SportsTeam.ROWING);
        ArrayList<Student> roster = new ArrayList<>();
        roster.add(a);
        roster.add(b);

        Admin admin = new Admin(roster, 2.0, 3.0);

        // Add a 1-hour session to student with ID 9
        LocalDateTime t1 = LocalDateTime.of(2024,4,1,9,0);
        LocalDateTime t2 = LocalDateTime.of(2024,4,1,10,0);
        admin.addSession(9, new Session(t1, t2));

        // Verify that student b (id 9) now has the correct total hours
        double[] bRes = b.calculateWeekHours(LocalDate.of(2024,4,7), 2.0, 3.0);
        assertEquals(1.0, bRes[0], 1e-6);
    }
}
