package edu.loyola.sghersick.A3_BugHunt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Student {
	//Attributes
	private int studentId;
	private String name;
	private SportsTeam sport;
	private ArrayList<Session> sessions;
	
	//Constructors
	public Student(int studentId, String name, SportsTeam sport) {
		this.studentId = studentId;
		this.name = name;
		this.sport = sport;
		
		// Default value
		this.sessions = new ArrayList<Session>();
	}
	
	/**
	 * Allow administrators to check if this is correct student from id
	 * @param studentId to check
	 * @return true if ID's match, else false
	 */
	public boolean isId(int studentId) {
		return studentId == studentId;
	}
	
	/**
	 * Add a session to student history
	 * @param session to add
	 */
	public void addSession(Session s) {
		sessions.put(s);
	}
	
	/**
	 * Calculate student's Week Hours & Banked Hours
	 * @param deadline for the week (inclusive)
	 * @param weeklyRequirement for study hours
	 * @param maxPerDay before extra get banked
	 * @return arr[0] = week hours, arr[1] = banked hours
	 */
	public double[] calculateWeekHours(LocalDate deadline, double weeklyRequirement, double maxPerDay) {
		double weekHours = 0, bankedHours = 0;
		HashMap<LocalDate, Double> dailyHours = new HashMap<>(); // Monitor hours in each day
	
		// Get hours for each day of target week
		for(Session s : sessions) {
			// Check that session is within week of deadline
			LocalDate sessionDate = s.getDate();
			boolean inWeek = sessionDate.compareTo(deadline) < 0 && sessionDate.compareTo(deadline.minusDays(7)) > 0;
			
			if(inWeek) {
				double dayHours = dailyHours.getOrDefault(sessionDate, 0.0) + s.calcDuration();
				
				// Check for excessive hours within day
				if(dayHours > maxPerDay) {
					bankedHours += dayHours - maxPerDay;
					dayHours = maxPerDay;
				}
				
				// Update daily + weekly hours
				dailyHours.put(sessionDate, dayHours);
			}
		}
		
		// Sum up week hours
		for(Map.Entry<LocalDate, Double> set : dailyHours.entrySet()) {
			weekHours += set.getValue();
		}
		
		// Check excess week hours
		if(weekHours > weeklyRequirement) {
			bankedHours += weekHours - weeklyRequirement;
		}
		
		double[] values = {weekHours, bankedHours}; // hours[0] = week hours, hours[1] = banked hours
		return values;
	}
	
	@Override
	public String toString() {
		return "<"+studentId+"> "+name+" "+sport;
	}
	
}
