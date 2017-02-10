package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Availability {
	
	public enum DayOfWeek { 
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY 
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Enumerated(EnumType.STRING)
	@Column(name="day")
	private String dayOfWeek;
	
	@Column(name="am")
	private boolean freeAM;
	
	@Column(name="pm")
	private boolean freePM;
	
	public Availability() { }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Availability [id=");
		builder.append(id);
		builder.append(", dayOfWeek=");
		builder.append(dayOfWeek);
		builder.append(", freeAM=");
		builder.append(freeAM);
		builder.append(", freePM=");
		builder.append(freePM);
		builder.append("]");
		return builder.toString();
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public boolean isFreeAM() {
		return freeAM;
	}

	public void setFreeAM(boolean freeAM) {
		this.freeAM = freeAM;
	}

	public boolean isFreePM() {
		return freePM;
	}

	public void setFreePM(boolean freePM) {
		this.freePM = freePM;
	}

	public int getId() {
		return id;
	}
	
}
