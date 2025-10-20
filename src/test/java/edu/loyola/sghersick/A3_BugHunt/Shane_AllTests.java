package edu.loyola.sghersick.A3_BugHunt;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Shane_AllTests {

    // =======================
    // Session Tests
    // =======================
    @Test
    @DisplayName("Session: calcDuration for 90 minutes should equal 1.5 hours")
    void session_calcDuration_basic() {
        LocalDateTime in = LocalDateTime.of(2024, 3, 4, 13, 0);
        LocalDateTime out = LocalDateTime.of(2024, 3, 4, 14, 30);
        Session s = new Session(in, out);
        assertEquals(1.5, s.calcDuration(), 1e-6);
        assertEquals(LocalDate.of(2024, 3, 4), s.getDate());
    }

    @Test
    @DisplayName("Session: checkout before checkin should throw")
    void session_invalidCheckout() {
        LocalDateTime in = LocalDateTime.of(2024, 1, 10, 12, 0);
        LocalDateTime out = LocalDateTime.of(2024, 1, 10, 11, 59);
        assertThrows(IllegalArgumentException.class, () -> new Session(in, out));
    }

    @Test
    @DisplayName("Session: cross midnight should throw")
    void session_crossMidnight() {
        LocalDateTime in = LocalDateTime.of(2024, 1, 10, 23, 30);
        LocalDateTime out = LocalDateTime.of(2024, 1, 11, 0, 30);
        assertThrows(IllegalArgumentException.class, () -> new Session(in, out));
    }

    @Test
    @DisplayName("Session: zero-length should not be negative")
    void session_zeroLength() {
        LocalDateTime t = LocalDateTime.of(2024, 1, 10, 12, 0);
        try {
            Session s = new Session(t, t);
            assertTrue(s.calcDuration() >= 0);
        } catch (IllegalArgumentException ok) {
            // acceptable behavior
        }
    }

    // =======================
    // Student Tests
    // =======================
    @Test
    @DisplayName("Student: exact requirement met, no banked hours")
    void student_weeklyExact() {
        Student st = new Student(101, "Test Student", SportsTeam.SOCCER);
        LocalDate deadline = LocalDate.of(2024, 3, 10);
        st.addSession(new Session(LocalDateTime.of(2024, 3, 8, 13, 0),
                                  LocalDateTime.of(2024, 3, 8, 14, 0)));
        st.addSession(new Session(LocalDateTime.of(2024, 3, 9, 16, 0),
                                  LocalDateTime.of(2024, 3, 9, 18, 0)));
        st.addSession(new Session(LocalDateTime.of(2024, 3, 10, 10, 0),
                                  LocalDateTime.of(2024, 3, 10, 11, 0)));

        double[] res = st.calculateWeekHours(deadline, 4.0, 4.0);
        assertEquals(4.0, res[0], 1e-6);
        assertEquals(0.0, res[1], 1e-6);
    }

    @Test
    @DisplayName("Student: daily cap and banking works correctly")
    void student_perDayCap() {
        Student st = new Student(102, "Cap Student", SportsTeam.BASKETBALL);
        LocalDate deadline = LocalDate.of(2024, 3, 10);
        st.addSession(new Session(LocalDateTime.of(2024, 3, 7, 9, 0),
                                  LocalDateTime.of(2024, 3, 7, 12, 0)));
        st.addSession(new Session(LocalDateTime.of(2024, 3, 7, 12, 30),
                                  LocalDateTime.of(2024, 3, 7, 15, 30)));

        double[] res = st.calculateWeekHours(deadline, 3.0, 4.0);
        assertEquals(3.0, res[0], 1e-6);
        assertEquals(1.0, res[1], 1e-6);
    }

    @Test
    @DisplayName("Student: sessions outside 7-day window are ignored")
    void student_excludeOutsideWindow() {
        Student st = new Student(103, "Window Student", SportsTeam.TENNIS);
        LocalDate deadline = LocalDate.of(2024, 3, 10);
        st.addSession(new Session(LocalDateTime.of(2024, 3, 5, 10, 0),
                                  LocalDateTime.of(2024, 3, 5, 12, 0)));
        st.addSession(new Session(LocalDateTime.of(2024, 3, 10, 10, 0),
                                  LocalDateTime.of(2024, 3, 10, 11, 0)));
        st.addSession(new Session(LocalDateTime.of(2024, 3, 3, 12, 0),
                                  LocalDateTime.of(2024, 3, 3, 13, 0)));

        double[] res = st.calculateWeekHours(deadline, 10.0, 8.0);
        assertEquals(3.0, res[0], 1e-6);
        assertEquals(0.0, res[1], 1e-6);
    }

    // =======================
    // Admin Tests
    // =======================
    private static Student mk(int id, String name, SportsTeam t) {
        return new Student(id, name, t);
    }

    @Test
    @DisplayName("Admin: getSlackers only returns below requirement")
    void admin_slackersOnly() {
        Student a = mk(1, "Alice", SportsTeam.GOLF);
        Student b = mk(2, "Bob", SportsTeam.SOCCER);
        Student c = mk(3, "Cyd", SportsTeam.LACROSSE);
        LocalDate deadline = LocalDate.of(2024, 3, 10);

        a.addSession(new Session(LocalDateTime.of(2024, 3, 9, 10, 0),
                                 LocalDateTime.of(2024, 3, 9, 13, 0)));
        b.addSession(new Session(LocalDateTime.of(2024, 3, 8, 10, 0),
                                 LocalDateTime.of(2024, 3, 8, 12, 0)));
        b.addSession(new Session(LocalDateTime.of(2024, 3, 10, 12, 0),
                                 LocalDateTime.of(2024, 3, 10, 12, 30)));
        c.addSession(new Session(LocalDateTime.of(2024, 3, 7, 9, 0),
                                 LocalDateTime.of(2024, 3, 7, 11, 0)));
        c.addSession(new Session(LocalDateTime.of(2024, 3, 10, 9, 0),
                                 LocalDateTime.of(2024, 3, 10, 11, 30)));

        ArrayList<Student> roster = new ArrayList<>();
        roster.add(a); roster.add(b); roster.add(c);

        Admin admin = new Admin(roster, 3.0, 4.0);
        HashMap<Student,double[]> slackers = admin.getSlackers(deadline);

        assertEquals(1, slackers.size());
        assertTrue(slackers.containsKey(b));
        assertFalse(slackers.containsKey(a));
        assertFalse(slackers.containsKey(c));
    }

    @Test
    @DisplayName("Admin: addSession adds to correct student by ID")
    void admin_addSessionById() {
        Student a = mk(7, "Seven", SportsTeam.SWIMMING);
        Student b = mk(9, "Nine", SportsTeam.ROWING);
        ArrayList<Student> roster = new ArrayList<>();
        roster.add(a); roster.add(b);

        Admin admin = new Admin(roster, 2.0, 3.0);
        LocalDateTime t1 = LocalDateTime.of(2024, 4, 1, 9, 0);
        LocalDateTime t2 = LocalDateTime.of(2024, 4, 1, 10, 0);

        admin.addSession(9, new Session(t1, t2));
        double[] bRes = b.calculateWeekHours(LocalDate.of(2024, 4, 7), 2.0, 3.0);
        assertEquals(1.0, bRes[0], 1e-6);
    }
}
