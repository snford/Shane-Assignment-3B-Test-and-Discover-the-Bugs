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
    	if(!verifySession(checkIn, checkOut)) {
    		throw new IllegalArgumentException("Invalid times");
    	}
    }
    
    /**
     * Verify a session is valid
     * @param t1 start time
     * @param t2 end time
     * @return true if valid, else false
     */
    private boolean verifySession(LocalDateTime t1, LocalDateTime t2) {
    	// check if either is null
    	if(t1 == null && t2 == null) return false;
    	// check-in after or same time as check-out
    	if(t1.isAfter(t2)) return false;
    	// check spanning multiple days
    	if(!t1.toLocalDate().isEqual(t2.toLocalDate())) return false;
    	
    	checkIn = t1;
    	checkOut = t2;
    	return true;
    }
    
    /**
     * Get date of session
     */
    public LocalDate getDate() {
    	return checkOut.toLocalDate();
    }
    
    /** 
     * Calculate the duration of a session
     * @return session duration (hours) on success. Return negative on failure.
     */
    public double calcDuration() {
  	    long seconds = ChronoUnit.SECONDS.between(checkIn, checkOut);
	    return seconds/60.0;
    }
    
    /**
     * Print out the Session data
     * @return session data
     */
    @Override
    public String toString() {
    	double duration = calcDuration();
    	return "{CheckIn: "+ checkIn+", CheckOut: " +checkOut+", Duration: "+ (duration >= 0 ? duration : "N/A")+"}";
    }
}
