package edu.loyola.sghersick.A3_BugHunt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Session
{
    // Attributes
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    // Constructor
    public Session(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (!verifySession(checkIn, checkOut)) {
            throw new IllegalArgumentException("Invalid session timestamps");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    // Verify that session is valid
    // (keep signature; only fixed body)
    private boolean verifySession(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) return false;
        if (!t1.toLocalDate().equals(t2.toLocalDate())) return false;   // same calendar day
        return t2.isAfter(t1);                                           // strictly after (reject zero/negative)
    }

    // Get date of the session
    public LocalDate getDate() {
        return checkIn.toLocalDate();
    }

    // Calculate duration in **hours** (double)
    public double calcDuration() {
        long minutes = ChronoUnit.MINUTES.between(checkIn, checkOut);
        if (minutes < 0) return 0.0;   // defensive; verifySession prevents this
        return minutes / 60.0;         // convert minutes -> hours
    }

    // Print this session's information
    public String toString() {
        return "Session on "+getDate()+" for "+String.format("%.2f", calcDuration())+" hours";
    }
}
